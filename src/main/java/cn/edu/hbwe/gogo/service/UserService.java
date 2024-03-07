package cn.edu.hbwe.gogo.service;

import cn.edu.hbwe.gogo.entity.LoginAuthorization;
import cn.edu.hbwe.gogo.exception.LoginException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cn.edu.hbwe.gogo.dao.UserDao;
import cn.edu.hbwe.gogo.utils.B64;
import cn.edu.hbwe.gogo.utils.HTTPUtil;
import cn.edu.hbwe.gogo.utils.RSAEncoder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;
    // 引入Log4j2日志 日志记录器
    private static final Logger logger = LogManager.getLogger(UserService.class);
    // 引入LoginAuthorization类实例
    private final LoginAuthorization auth = new LoginAuthorization();

    public boolean login(String stuNum, String password) throws Exception {
        try {
        // 调用init方法，并使用LoginAuthorization对象存储数据
        init(auth);

        // 加密密码并使用LoginAuthorization对象中的公钥信息
        password = RSAEncoder.RSAEncrypt(password, B64.b64tohex(auth.getPublicKey().get("modulus")), B64.b64tohex(auth.getPublicKey().get("exponent")));
        password = B64.hex2b64(password);

        // 创建请求头和请求数据
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        Map<String, String> data = new HashMap<>();
        data.put("csrftoken", auth.getCsrf());
        data.put("yhm", stuNum);
        data.put("mm", password);

        // 使用HTTPUtil发送POST请求，传递LoginAuthorization的cookies
        Connection.Response response = HTTPUtil.sendPostRequest("/jwglxt/xtgl/login_slogin.html", headers, data, auth.getCookies());

        // 更新登录成功后的cookies
        auth.getCookies().put("JSESSIONID", response.cookie("JSESSIONID"));
        Document document = Jsoup.parse(response.body());
        // 判断是否登录成功
        return document.getElementById("tips") == null;
        } catch (Exception e) {
            logger.error("登录失败，原因：{}", e.getMessage());
            throw new LoginException("登录过程出现错误：" + e.getMessage(), e);
        }
    }

    // 定义一个查询课表的方法，接收学年和学期，返回一个字符串表示课表内容
    public String getTimetable(int year, int term) throws Exception {
        // 创建请求头和请求数据
        Map<String, String> headers = createCommonHeaders();
        Map<String, String> data = new HashMap<>();
        data.put("xnm", String.valueOf(year));
        data.put("xqm", String.valueOf(term * term * 3));
        // 使用HTTPUtil发送POST请求
        Connection.Response response = HTTPUtil.sendPostRequest("/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151", headers, data, auth.getCookies());
        // 解析返回的 JSON 数据，获取课表内容
        JSONObject jsonObject = JSON.parseObject(response.body());
        JSONArray kbList = jsonObject.getJSONArray("kbList");
        StringBuilder sb = new StringBuilder();
        // 遍历课表列表，拼接课表信息
        for (int i = 0; i < kbList.size(); i++) {
            JSONObject kb = kbList.getJSONObject(i);
            sb.append("课程名称：").append(kb.getString("kcmc")).append("\n");
            sb.append("上课时间：").append(kb.getString("xqjmc")).append("第").append(kb.getString("jcs")).append("节\n");
            sb.append("上课地点：").append(kb.getString("cdmc")).append("\n");
            sb.append("任课教师：").append(kb.getString("xm")).append("\n");
            sb.append("周次：").append(kb.getString("zcd")).append("\n");
            sb.append("------------------------------\n");
        }
        // 返回课表信息
        return sb.toString();
    }

    public boolean logout() {
        try {
            // 创建请求头
            Map<String, String> headers = createCommonHeaders();
            // 使用HTTPUtil发送POST请求
            Connection.Response response = HTTPUtil.sendPostRequest("/jwglxt/logout", headers, null, auth.getCookies());
            // 清除登录产生的缓存
            auth.getCookies().clear();
            return response.statusCode() == 200;
        } catch (Exception e) {
            throw new LoginException("登出过程中发生错误：" + e.getMessage(), e);
        }
    }

    // 修改初始化方法以适应LoginAuthorization类
    public void init(LoginAuthorization auth) {
        // 确保auth不为空并且其cookies已初始化
        if (auth != null && auth.getCookies() != null) {
            try {
                getCsrftoken(auth);
                getRSApublickey(auth);
            } catch (Exception e) {
                logger.error("Error during initialization", e);
                // 清除已获取的数据，避免使用到错误的数据
                auth.setCsrf(null);
                auth.getPublicKey().clear();
            }
        } else {
            throw new IllegalArgumentException("LoginAuthorization instance or its cookies must not be null");
        }
    }

    private Map<String, String> createCommonHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        return headers;
    }

    // 修改获取csrftoken和Cookies的方法
    private void getCsrftoken(LoginAuthorization auth) throws Exception {
        try {
            // 创建请求头
            System.out.println("getCsrftoken");
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0");
            // 使用HTTPUtil发送GET请求
            Connection.Response response = HTTPUtil.sendGetRequest("/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t=" + new Date().getTime(), headers, auth.getCookies());
            Map<String, String> cookies = auth.getCookies();
            auth.getCookies().put("JSESSIONID", response.cookie("JSESSIONID"));
            auth.getCookies().put("route", response.cookie("route"));
            Document document = Jsoup.parse(response.body());
            auth.setCsrf(Objects.requireNonNull(document.getElementById("csrftoken")).val());
            System.out.println(auth.getCsrf());
        } catch (Exception e) {
            logger.error("Failed to get csrftoken", e);
        }
    }

    // 修改获取公钥的方法
    private void getRSApublickey(LoginAuthorization auth) throws Exception {
        try {
            // 创建请求头
            System.out.println("getRSApublickey");
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0");
            // 使用HTTPUtil发送GET请求
            Connection.Response response = HTTPUtil.sendGetRequest("/jwglxt/xtgl/login_getPublicKey.html?" +
                    "time=" + new Date().getTime(), headers, auth.getCookies());
            JSONObject jsonObject = JSON.parseObject(response.body());
            auth.getPublicKey().put("modulus", jsonObject.getString("modulus"));
            auth.getPublicKey().put("exponent", jsonObject.getString("exponent"));
            System.out.println(auth.getPublicKey().get("modulus"));
            System.out.println(auth.getPublicKey().get("exponent"));
        } catch (Exception e) {
            logger.error("Failed to get RSA public key", e);
        }
    }
}
