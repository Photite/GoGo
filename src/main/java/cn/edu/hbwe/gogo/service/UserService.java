package cn.edu.hbwe.gogo.service;

import cn.edu.hbwe.gogo.dao.UserDao;
import cn.edu.hbwe.gogo.utils.B64;
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

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    private final String url = "https://jwxt.hbwe.edu.cn";
    private Map<String, String> cookies = new HashMap<>();
    private String modulus;
    private String exponent;
    private String csrftoken;
    private Connection connection;
    private Connection.Response response;
    private Document document;

    // 定义一个登录的方法，接收学号和密码，返回一个布尔值表示是否登录成功
    public boolean login(String stuNum, String password) throws Exception {
        // 调用 init 方法，获取 csrftoken 和公钥
        init();
        // 加密密码
        password = RSAEncoder.RSAEncrypt(password, B64.b64tohex(modulus), B64.b64tohex(exponent));
        password = B64.hex2b64(password);
        // 发送登录请求
        connection = Jsoup.connect(url + "/jwglxt/xtgl/login_slogin.html");
        connection.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.data("csrftoken", csrftoken);
        connection.data("yhm", stuNum);
        connection.data("mm", password);
        connection.data("mm", password);
        response = connection.cookies(cookies).ignoreContentType(true)
                .method(Connection.Method.POST).execute();
        cookies.put("JSESSIONID", response.cookie("JSESSIONID"));
        response = connection.execute();
        document = Jsoup.parse(response.body());
        // 判断是否登录成功
        if (document.getElementById("tips") == null) {
            return true;
        } else {
            return false;
        }
    }

    // 定义一个查询课表的方法，接收学年和学期，返回一个字符串表示课表内容
    public String getTimetable(int year, int term) throws Exception {
        // 发送查询课表的请求
        connection = Jsoup.connect(url + "/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.data("xnm", String.valueOf(year));
        connection.data("xqm", String.valueOf(term * term * 3));
        response = connection.cookies(cookies).method(Connection.Method.POST).ignoreContentType(true).execute();
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
            Connection connection = Jsoup.connect(url + "/jwglxt/logout");
            connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
            Connection.Response response = connection.cookies(cookies).ignoreContentType(true).execute();
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    // 定义一个初始化的方法，获取 csrftoken 和公钥
    private void init() throws Exception {
        getCsrftoken();
        getRSApublickey();
    }

    // 定义一个获取 csrftoken 和 Cookies 的方法
    private void getCsrftoken() throws Exception {
        connection = Jsoup.connect(url + "/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t=" + new Date().getTime());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0");
        response = connection.execute();
        cookies.put("JSESSIONID", response.cookie("JSESSIONID"));
        cookies.put("route", response.cookie("route"));
        document = Jsoup.parse(response.body());
        csrftoken = document.getElementById("csrftoken").val();
    }

    // 定义一个获取公钥的方法
    private void getRSApublickey() throws Exception {
        connection = Jsoup.connect(url + "/jwglxt/xtgl/login_getPublicKey.html?" +
                "time=" + new Date().getTime());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0");
        response = connection.cookies(cookies).ignoreContentType(true).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        modulus = jsonObject.getString("modulus");
        exponent = jsonObject.getString("exponent");
    }

}
