package cn.edu.hbwe.gogo.service;

import cn.edu.hbwe.gogo.dao.UserDao;
import cn.edu.hbwe.gogo.entity.*;
import cn.edu.hbwe.gogo.exception.LoginException;
import cn.edu.hbwe.gogo.utils.B64;
import cn.edu.hbwe.gogo.utils.HTTPUtil;
import cn.edu.hbwe.gogo.utils.RSAEncoder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Photite
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;
    // 引入Log4j2日志 日志记录器
    private static final Logger logger = LogManager.getLogger(UserService.class);
    // 引入LoginAuthorization类实例
    private final LoginAuthorization auth = new LoginAuthorization();

    public User login(String username, String password) {
        String pwd = DigestUtils.md5Hex(password + username);
        // 使用QueryWrapper构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("password", pwd);
        // 调用UserDao的selectOne方法来进行用户登录验证
        return userDao.selectOne(queryWrapper);
    }

    public boolean register(String username, String password) {
        String pwd = DigestUtils.md5Hex(password + username);
        int i = userDao.insert(new User(username, pwd));
        return i > 0;
    }

    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userDao.selectOne(queryWrapper);
    }

    public boolean stuLogin(String stuNum, String password) {
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

    public List<ClassUnit> getClassTable(String stuNum) throws Exception {
        System.out.println(stuNum);
        YearAndSemestersPicker picker = getPicker(stuNum);
        Map<String, String> headers = createCommonHeaders();
        Term term = picker.getDefaultTerm();
        //输出picker
        System.out.println(picker);
        System.out.println("学期：" + term);

        String xnm = picker.getYears().get(term.getYearsOfSchooling());
        String xqm = picker.getSemesters().get(term.getSemesterNumber());
        System.out.println("学年：" + xnm);
        System.out.println("学期：" + xqm);

        Map<String, String> data = new HashMap<>();
        data.put("xnm", String.valueOf(picker.getYears().get(term.getYearsOfSchooling())));
//        data.put("xqm", String.valueOf(picker.getSemesters().get(term.getSemesterNumber())));
        data.put("xqm", "3");

        Connection.Response response = HTTPUtil.sendPostRequest("/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151", headers, data, auth.getCookies());
        System.out.println("发送了请求");

        String body = response.body();
        assertLogin(Jsoup.parse(body));


        com.alibaba.fastjson2.JSONArray array = com.alibaba.fastjson2.JSON.parseObject(body).getJSONArray("kbList");
        if (array.isEmpty()) {
            throw new IllegalStateException("该学年学期的课表尚未开放!");
        }
        return array.stream()
                .map((v) -> {
                    com.alibaba.fastjson2.JSONObject a = (com.alibaba.fastjson2.JSONObject) v;
                    String lesson = a.getString("jcs");
                    String[] ls = lesson.split("-");

                    return new ClassUnit(
                            a.getString("kcmc"),
                            a.getString("xm"),
                            a.getString("cdmc"),
                            a.getString("zcd"),
                            new ClassUnit.Range(Integer.parseInt(ls[0]), Integer.parseInt(ls[1]), ClassUnit.FilterType.ALL),
                            a.getString("kch_id"),
                            a.getString("xqj")
                    );
                })
                .collect(Collectors.toList());
    }

    // 定义一个查询学校日期的方法（包括：学期起止时间，当前学期数），接收学号和cookie，返回SchoolCalender
    public SchoolCalender getSchoolCalender(String stuNum) throws Exception {
        Objects.requireNonNull(stuNum);
        Objects.requireNonNull(auth.getCookies());
        Map<String, String> headers = createCommonHeaders();

        Connection.Response response = HTTPUtil.sendGetRequest("/jwglxt/xtgl/index_cxAreaSix.html?localeKey=zh_CN&gnmkdm=index&su=" + stuNum, headers, auth.getCookies());

        Document document = Jsoup.parse(response.body());
        assertLogin(document);
        String source = document.getElementsByAttributeValue("colspan", "23").get(0).text();

        String year = source.split("学年")[0];
        String sem = source.split("学年")[1].split("学期")[0];


        int l, r;
        l = source.indexOf("(");
        r = source.indexOf(")");
        source = source.substring(l + 1, r);
        String[] se = source.split("至");

        String[] starts = se[0].split("-");
        LocalDate start = LocalDate.of(Integer.parseInt(starts[0]), Integer.parseInt(starts[1]), Integer.parseInt(starts[2]));
        starts = se[1].split("-");
        LocalDate end = LocalDate.of(Integer.parseInt(starts[0]), Integer.parseInt(starts[1]), Integer.parseInt(starts[2]));

        Term term1 = new Term(year, sem);

        return new SchoolCalender(start, end, term1);
    }

    public List<ExamResult> getExamList(String stuNum, int all) throws Exception {
        System.out.println(all + "all");
        YearAndSemestersPicker picker = getPicker(stuNum);
        Term term = picker.getDefaultTerm();
        String xnm = picker.getYears().get(term.getYearsOfSchooling());
        String xqm;
        if (all == 1) {
            xqm = "";
        } else {
//        String xqm = picker.getSemesters().get(term.getSemesterNumber());
            xqm = "3";
        }
        System.out.println("学期：" + xqm);
        try {
            Objects.requireNonNull(xnm);
            Objects.requireNonNull(xqm);
        } catch (NullPointerException e) {
            throw new IllegalStateException("学期值非法!");
        }
        Map<String, String> headers = createCommonHeaders();

        Map<String, String> data = new HashMap<>();
        data.put("xnm", xnm);
        data.put("xqm", xqm);
        data.put("kcbj", "");
        data.put("_search", "false");
        data.put("nd", String.valueOf(new Date().getTime()));
        data.put("queryModel.showCount", "15");
        data.put("queryModel.currentPage", "1");
        data.put("queryModel.sortName", "");
        data.put("queryModel.sortOrder", "asc");
        data.put("time", "2");

        Connection.Response response = HTTPUtil.sendPostRequest("/jwglxt/cjcx/cjcx_cxXsgrcj.html?doType=query&gnmkdm=N305005&su=" + stuNum, headers, data, auth.getCookies());

        String body = response.body();
        assertLogin(Jsoup.parse(body));

        String items = com.alibaba.fastjson2.JSON.parseObject(body).getJSONArray("items").toString();

        List<com.alibaba.fastjson2.JSONObject> jsonObjects = com.alibaba.fastjson2.JSON.parseArray(items, com.alibaba.fastjson2.JSONObject.class);

        // 遍历JSONObject列表
        for (com.alibaba.fastjson2.JSONObject jsonObject : jsonObjects) {
            // 获取sfxwkc字段的值
            String sfxwkc = jsonObject.getString("sfxwkc");
            // 将"是"转换为true，将"否"转换为false
            jsonObject.put("sfxwkc", "是".equals(sfxwkc));
        }

        return jsonObjects.stream()
                .map(jsonObject -> com.alibaba.fastjson2.JSON.toJavaObject(jsonObject, ExamResult.class))
                .collect(Collectors.toList());
    }

    public Profile getUserProfile(String stuNum) throws Exception {
        Objects.requireNonNull(stuNum);
        Objects.requireNonNull(auth.getCookies());

        Map<String, String> headers = createCommonHeaders();

        Connection.Response response = HTTPUtil.sendGetRequest("/jwglxt/xsxxxggl/xsgrxxwh_cxXsgrxx.html?gnmkdm=N100801&layout=default&su=" + stuNum, headers, auth.getCookies());

        Document document = Jsoup.parse(response.body());
        assertLogin(document);

        String avt = document.getElementsByTag("img").get(0).attr("src");
        byte[] a = HTTPUtil.sendGetRequest(avt, headers, auth.getCookies()).bodyAsBytes();

        Elements ele = document.getElementsByClass("form-control-static");

        return new Profile(
                ele.get(0).text(),
                ele.get(1).text(),
                ele.get(23).text(),
                ele.get(24).text(),
                ele.get(26).text(),
                ele.get(9).text()

        );
    }

    public YearAndSemestersPicker getPicker(String stuNum) throws Exception {
        System.out.println("获取了默认学期");
        HashMap<String, String> years = new HashMap<>();
        HashMap<String, String> semesters = new HashMap<>();
        String defaultYears = null;
        String defaultTeamVal = null;

        Map<String, String> headers = createCommonHeaders();

        Connection.Response response = HTTPUtil.sendGetRequest("/jwglxt/cjcx/cjcx_cxDgXscj.html?gnmkdm=N305005&layout=default&su=" + stuNum, headers, auth.getCookies());
        System.out.println("发送了请求");
        assertLogin(Jsoup.parse(response.body()));
        Document document = Jsoup.parse(response.body());

        assertLogin(document);

        for (Element e : Objects.requireNonNull(document.getElementById("xnm")).getElementsByTag("option")) {

            if (e.attr("selected").equals("selected")) {
                defaultYears = e.text();
            }
            years.put(e.text(), e.attr("value"));
        }

        for (Element e : Objects.requireNonNull(document.getElementById("xqm")).getElementsByTag("option")) {
            if (!e.attr("selected").isEmpty()) {
                defaultTeamVal = e.text();
            }
            semesters.put(e.text(), e.attr("value"));
        }
        Term term = new Term(defaultYears, defaultTeamVal);
        return new YearAndSemestersPicker(years, semesters, term);
    }

    public List<List<String>> getExamInfo(String stuNum, ExamResult i) throws Exception {
        String xnm = i.getYear();
        String xqm = i.getSemester();
        Map<String, String> headers = createCommonHeaders();

        Map<String, String> data = new HashMap<>();
        data.put("xnm", xnm);
        data.put("xqm", xqm);
        data.put("kcmc", i.getName());
        data.put("jxb_id", i.getDetailsID());

        Connection.Response response = HTTPUtil.sendPostRequest("/jwglxt/cjcx/cjcx_cxCjxqGjh.html?time=" + new Date().getTime() + "&gnmkdm=N305005&su=" + stuNum, headers, data, auth.getCookies());
        List<List<String>> rtn = new ArrayList<>();
        String body = response.body();
        System.out.println(body);
        assertLogin(Jsoup.parse(body));
        Elements tr = Jsoup.parse(body).getElementsByTag("tr");
        for (int j = 1; j < tr.size(); j++) {
            List<String> trs = new ArrayList<>();
            for (Element td : tr.get(j).getElementsByTag("td")) {
                trs.add(td.text());
            }
            rtn.add(trs);
        }
        return rtn;
    }

    public List<ExamTimeAndPlace> getExamTimeAndplace(String stuNum) throws Exception {
        YearAndSemestersPicker picker = getPicker(stuNum);
        Term term = picker.getDefaultTerm();
        String xnm = "2022";
//        String xnm = picker.getYears().get(term.getYearsOfSchooling());
        String xqm = picker.getSemesters().get(term.getSemesterNumber());


        try {
            Objects.requireNonNull(xnm);
            Objects.requireNonNull(xqm);
        } catch (NullPointerException e) {
            throw new IllegalStateException("学期值非法!");
        }
        Map<String, String> headers = createCommonHeaders();

        Map<String, String> data = new HashMap<>();
        data.put("xnm", xnm);
        data.put("xqm", xqm);
        data.put("kcbj", "");
        data.put("_search", "false");
        data.put("nd", String.valueOf(new Date().getTime()));
        data.put("queryModel.showCount", "15");
        data.put("queryModel.currentPage", "1");
        data.put("queryModel.sortName", "");
        data.put("queryModel.sortOrder", "asc");
        data.put("time", "2");

        Connection.Response response = HTTPUtil.sendPostRequest("/jwglxt/kwgl/kscx_cxXsksxxIndex.html?doType=query&gnmkdm=N358105&su=" + stuNum, headers, data, auth.getCookies());

        String body = response.body();
        assertLogin(Jsoup.parse(body));

        String items = com.alibaba.fastjson2.JSON.parseObject(body).getJSONArray("items").toString();

        List<com.alibaba.fastjson2.JSONObject> jsonObjects = com.alibaba.fastjson2.JSON.parseArray(items, com.alibaba.fastjson2.JSONObject.class);

        // 遍历JSONObject列表
        for (com.alibaba.fastjson2.JSONObject jsonObject : jsonObjects) {
            // 获取sfxwkc字段的值
            String sfxwkc = jsonObject.getString("zxbj");
            // 将"是"转换为true，将"否"转换为false
            jsonObject.put("zxbj", "是".equals(sfxwkc));
        }
        System.out.println(jsonObjects);

        return jsonObjects.stream()
                .map(jsonObject -> com.alibaba.fastjson2.JSON.toJavaObject(jsonObject, ExamTimeAndPlace.class))
                .collect(Collectors.toList());
    }

    public boolean logout() {
        try {
            if (auth.getCookies() == null || auth.getCookies().isEmpty()) {
                return false;
            }
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

    public void assertLogin(Document doc) {
        for (Element e : doc.getElementsByTag("h5")) {
            if (e.text().equals("用户登录")) {

                Element test = doc.getElementById("tips");

                if (test != null) {
                    throw new LoginException.CookieOutOfDate(test.text());
                }

                throw new LoginException.CookieOutOfDate();
            }
        }
    }

    @SneakyThrows
    public void assertLogin(String cookie) {
        assertLogin(HTTPUtil.newSession("/xtgl/index_initMenu.html")
                .header("Cookie", cookie).get());
    }

    private Map<String, String> createCommonHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        return headers;
    }

    // 修改获取csrftoken和Cookies的方法
    private void getCsrftoken(LoginAuthorization auth) {
        try {
            // 创建请求头
            System.out.println("getCsrftoken");
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0");
            // 使用HTTPUtil发送GET请求
            Connection.Response response = HTTPUtil.sendGetRequest("/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t=" + new Date().getTime(), headers, auth.getCookies());
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
    private void getRSApublickey(LoginAuthorization auth) {
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


    public String getGPAScores(String stuNum) throws Exception {

        Map<String, String> headers = createCommonHeaders();


        String gpa = "";
        Connection.Response response = HTTPUtil.sendGetRequest("/jwglxt/xsxy/xsxyqk_cxXsxyqkIndex.html?gnmkdm=N105515&layout=default&su=" + stuNum, headers, auth.getCookies());
        String body = response.body();
        assertLogin(Jsoup.parse(body));
        System.out.println(Jsoup.parse(body));

        Document doc = Jsoup.parse(body);
        Elements elements = doc.select("font[style='color: red;']");  // 使用选择器定位所有红色的<font>元素

        for (Element element : elements) {
            String text = element.text().trim();
            try {
                Float.parseFloat(text);  // 尝试将文本转换为浮点数
                // 如果没有抛出异常，那么这个元素的文本就是GPA
                System.out.println("GPA: " + text);
                gpa = text;
                break;  // 找到GPA后就可以停止遍历
            } catch (NumberFormatException e) {
                // 如果抛出异常，那么这个元素的文本不是GPA，继续遍历下一个元素
            }
        }
        return gpa;
    }

}
