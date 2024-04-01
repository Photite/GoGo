package cn.edu.hbwe.gogo.controller;

import cn.edu.hbwe.gogo.dto.Result;
import cn.edu.hbwe.gogo.entity.*;
import cn.edu.hbwe.gogo.service.TokenService;
import cn.edu.hbwe.gogo.service.UserService;
import cn.edu.hbwe.gogo.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Photite
 */
@RestController
//@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/user")
public class UserController {

    // 注入 UserService
    @Autowired
    private UserService userService;

    //定义一个小程序用户登录的请求，接收用户名和密码
    @PostMapping("/login")
    public Result login(@RequestBody User object) {

        // 调用 UserService 的 login 方法，返回一个布尔值表示是否登录成功
        User user = userService.login(object.getUsername(), object.getPassword());
        Result vo;
        if (user != null) {
//            String token = tokenUtils.createToken(object.getId() + "", object.getUsername());
//            tokenService.saveToken(token, object.getId() + "");
            // 如果登录成功，返回 200 状态码和成功信息
            System.out.println("登录成功");
//            vo = new ResultVo<User>("登录成功", true, user, token);
            vo = new Result("登录成功", "1000", user);
        } else {
            // 如果登录失败，返回 401 状态码和失败信息
            System.out.println("登录失败");
            vo = new Result("账号或者密码错误", "2002", null);
        }
        return vo;
    }

    @PostMapping("/register")
    public Result register(@RequestBody User object) {
        boolean flag = userService.register(object.getUsername(), object.getPassword());
        Result vo;
        if (flag) {
            //注册成功
            vo = new Result("注册成功", "1000", null);
        } else {
            //注册成功
            vo = new Result("用户信息注册失败", "2002", null);
        }
        return vo;
    }

    // 定义一个模拟登录教务系统的请求，接收学号和密码
    @PostMapping("/stuLogin")
    public Result stulogin(@RequestBody User o) {
        userService.logout();
        Result vo;
        try {
            System.out.println("username: " + o.getJwxtUsername() + " password: " + o.getJwxtPassword());
            // 调用 UserService 的 login 方法，返回一个布尔值表示是否登录成功
            boolean result = userService.stuLogin(o.getJwxtUsername(), o.getJwxtPassword());
            if (result) {
                // 如果登录成功，返回 200 状态码和成功信息
                System.out.println("登录成功");
                vo = new Result("登录成功", "1000", null);
            } else {
                // 如果登录失败，返回 401 状态码和失败信息
                System.out.println("登录失败");
                vo = new Result("登录失败", "2002", null);
            }
            return vo;
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            return new Result("登录异常", "200", null);
        }
    }

    // 定义一个查询课表的请求，接收学年和学期
    @GetMapping("/getTimetable")
    public Result getTimetable(@RequestParam String jwxtUsername) {
        try {
            // 调用 UserService 的 getClassTable 方法，返回一个字符串表示课表内容
            List<ClassUnit> timetable = userService.getClassTable(jwxtUsername);
            ObjectMapper mapper = new ObjectMapper();
            String jsonResult = mapper.writeValueAsString(timetable);
            System.out.println(jsonResult);

            // 返回 200 状态码和课表内容
            System.out.println("获取课表成功");
            return new Result("获取课表成功", "1000", timetable);
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            System.out.println("获取课表失败");
            return new Result("获取课表失败", "2002", null);
        }
    }

    // 定义一个查询学校当前学期起止时间的请求
    @GetMapping("/getSchoolCalender")
    public Result getSchoolCalender(@RequestParam String jwxtUsername) {
        System.out.println("username: " + jwxtUsername);
        Result vo;
        try {
            // 调用 UserService 的 getSchoolCalender 方法，返回一个字符串表示学校日期
            SchoolCalender schoolCalender = userService.getSchoolCalender(jwxtUsername);

            // 创建 ObjectMapper 对象
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            // 将 schoolCalender 对象转换为 JSON 字符串
            String schoolCalenderJson = mapper.writeValueAsString(schoolCalender);

            System.out.println(schoolCalenderJson);
            // 返回 200 状态码和学校日期
            System.out.println("获取学校日期成功");
            vo = new Result("获取学校日期成功", "1000", schoolCalender);
            return vo;
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            System.out.println("获取学校日期失败");
            vo = new Result("获取学校日期失败", "2002", null);
            return vo;
        }
    }

    // 定义一个获取考试分数的请求
    @GetMapping("/getExamInfo")
    public Result getExamInfo(@RequestParam String jwxtUsername, @RequestParam int all) {
        try {
            // 调用 UserService 的 getExam 方法，返回一个字符串表示考试信息
            List<ExamResult> exam = userService.getExamList(jwxtUsername,all);
            List<Map<String, Object>> simplifiedExamResults = new ArrayList<>();
            for (ExamResult examResult : exam) {
                Map<String, Object> simplifiedExamResult = new HashMap<>();
                simplifiedExamResult.put("name", examResult.getName());
                simplifiedExamResult.put("absoluteScore", examResult.getAbsoluteScore());
                simplifiedExamResults.add(simplifiedExamResult);
            }
            // 返回 200 状态码和考试信息
            System.out.println("获取考试信息成功");
            return new Result("获取考试信息成功", "1000", simplifiedExamResults);

        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            System.out.println("获取考试信息失败");
            return new Result("获取考试信息失败", "2002", null);
        }
    }

    // 定义一个获取考试详情的请求
    @GetMapping("/getExamDetail")
    public ResponseEntity<String> getExamDetail(@RequestParam String stuNum) {
        try {
            //新建一个ExamResult对象
            ExamResult examResult = new ExamResult();
            //将ExamResult(year=2023, semester=3, detailsID=04BCA1F52B453589E0658AE7472DE879, name=大学生社会实践Ⅲ, teacher=魏荣华, credit=1.0, gradePoint=4.50, crTimesGp=4.50, absoluteScore=95, relateScore=优秀, completionCode=01, degreeProgram=false)赋值给examResult
            examResult.setYear("2023");
            examResult.setSemester("3");
            examResult.setDetailsID("04BCA1F52B453589E0658AE7472DE879");
            examResult.setName("大学生社会实践Ⅲ");
            examResult.setTeacher("魏荣华");
            examResult.setCredit("1.0");
            examResult.setGradePoint("4.50");
            examResult.setCrTimesGp("4.50");
            examResult.setAbsoluteScore("95");
            examResult.setRelateScore("优秀");
            examResult.setCompletionCode("01");
            examResult.setDegreeProgram(false);
            // 调用 UserService 的 getExamDetail 方法，返回一个字符串表示考试时间地点信息
            List<List<String>> examDetail = userService.getExamInfo(stuNum, examResult);
            // 返回 200 状态码和考试时间地点信息
            System.out.println("获取考试时间地点信息成功");
            return ResponseEntity.ok(examDetail.toString());
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            System.out.println("获取考试时间地点信息失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 定义一个获取考试时间地点的请求
    @GetMapping("/getExamTimeAndPlace")
    public ResponseEntity<String> getExamTimeAndPlace(@RequestParam String stuNum) {
        try {
            // 调用 UserService 的 getExamTimeAndPlace 方法，返回一个字符串表示考试时间地点信息
            List<ExamTimeAndPlace> examTimeAndPlace = userService.getExamTimeAndplace(stuNum);
            // 返回 200 状态码和考试时间地点信息
            System.out.println("获取考试时间地点信息成功");
            return ResponseEntity.ok(examTimeAndPlace.toString());
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            System.out.println("获取考试时间地点信息失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 定义一个获取用户信息的请求
    @GetMapping("/getUserProfile")
    public Result getUserProfile(@RequestParam String jwxtUsername) {
        try {
            // 调用 UserService 的 getProfile 方法，返回一个字符串表示用户信息
            Profile profile = userService.getUserProfile(jwxtUsername);
            // 创建一个列表来存储Info对象
            List<Info> infoList = new ArrayList<>();
            // 将Profile对象的每个字段转换为一个Info对象，并添加到列表中
            infoList.add(new Info("学号", profile.getNo()));
            infoList.add(new Info("姓名", profile.getName()));
            infoList.add(new Info("年级", profile.getGrade()));
            infoList.add(new Info("学院", profile.getCollegeName()));
            infoList.add(new Info("专业", profile.getStudyName()));
            infoList.add(new Info("身份证", profile.getIdCard()));
            String gpa = userService.getGPAScores(jwxtUsername);
            infoList.add(new Info("绩点", gpa));
            Map<String, Object> data = new HashMap<>();
            data.put("info", infoList);
            // 返回 200 状态码和用户信息
            System.out.println("获取用户信息成功");
            return new Result("获取用户信息成功", "1000", data);
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            System.out.println("获取用户信息失败");
            return new Result("获取用户信息失败", "2002", null);
        }
    }

    //定义一个获取GPA的请求
    @GetMapping("/getGPA")
    public ResponseEntity<String> getGPA(@RequestParam String stuNum) {
        try {
            // 调用 UserService 的 getGPA 方法，返回一个字符串表示GPA
            String gpa = userService.getGPAScores(stuNum);
            // 返回 200 状态码和GPA
            System.out.println("获取GPA成功");
            return ResponseEntity.ok(gpa);
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            System.out.println("获取GPA失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/stuLogout")
    public ResponseEntity<String> stuLogout() {
        boolean result = userService.logout();
        if (result) {
            System.out.println("退出成功");
            return ResponseEntity.ok("退出成功");
        } else {
            System.out.println("退出失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("退出失败");
        }
    }
}
