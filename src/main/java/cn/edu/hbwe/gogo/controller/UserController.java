package cn.edu.hbwe.gogo.controller;

import cn.edu.hbwe.gogo.entity.*;
import cn.edu.hbwe.gogo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/user")
public class UserController {

    // 注入 UserService
    @Autowired
    private UserService userService;

    //定义一个小程序用户登录的请求，接收用户名和密码
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User object) {
        try {
            // 调用 UserService 的 login 方法，返回一个布尔值表示是否登录成功
            User user = userService.login(object.getUsername(), object.getPassword());
            if (user != null) {
                // 如果登录成功，返回 200 状态码和成功信息
                System.out.println("登录成功");
                return ResponseEntity.ok("登录成功");
            } else {
                // 如果登录失败，返回 401 状态码和失败信息
                System.out.println("登录失败");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登录失败");
            }
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResultVo<User> Register(@RequestBody User object) {
        boolean flag = userService.Register(object.getUsername(), object.getPassword());
        ResultVo<User> vo = null;
        if (flag) {
            //注册成功
            vo = new ResultVo<>("注册成功", true, null);
        } else {
            //注册成功
            vo = new ResultVo<>("用户信息注册失败", false, null);
        }
        return vo;
    }

    // 定义一个模拟登录教务系统的请求，接收学号和密码
    @PostMapping("/stuLogin")
    public ResponseEntity<String> stulogin(@RequestParam String stuNum, @RequestParam String password) {
        try {
            // 调用 UserService 的 login 方法，返回一个布尔值表示是否登录成功
            boolean result = userService.stuLogin(stuNum, password);
            if (result) {
                // 如果登录成功，返回 200 状态码和成功信息
                System.out.println("登录成功");
                return ResponseEntity.ok("登录成功");
            } else {
                // 如果登录失败，返回 401 状态码和失败信息
                System.out.println("登录失败");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登录失败");
            }
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 定义一个查询课表的请求，接收学年和学期
    @GetMapping("/getTimetable")
    public ResponseEntity<String> getTimetable(@RequestParam String stuNum) {
        try {
            // 调用 UserService 的 getClassTable 方法，返回一个字符串表示课表内容
            List<ClassUnit> timetable = userService.getClassTable(stuNum);
            ObjectMapper mapper = new ObjectMapper();
            String jsonResult = mapper.writeValueAsString(timetable);
            System.out.println(jsonResult);

            // 返回 200 状态码和课表内容
            System.out.println("获取课表成功");
            return ResponseEntity.ok(timetable.toString());
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            System.out.println("获取课表失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 定义一个查询学校当前学期起止时间的请求
    @GetMapping("/getSchoolCalender")
    public ResponseEntity<String> getSchoolCalender(@RequestParam String stuNum) {
        try {
            // 调用 UserService 的 getSchoolCalender 方法，返回一个字符串表示学校日期
            SchoolCalender schoolCalender = userService.getSchoolCalender(stuNum);
            System.out.println(schoolCalender.toString());
            // 返回 200 状态码和学校日期
            System.out.println("获取学校日期成功");
            return ResponseEntity.ok(String.valueOf(schoolCalender));
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            System.out.println("获取学校日期失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 定义一个获取考试信息的请求
    @GetMapping("/getExamInfo")
    public ResponseEntity<String> getExamInfo(@RequestParam String stuNum) {
        try {
            // 调用 UserService 的 getExam 方法，返回一个字符串表示考试信息
            List<ExamResult> exam = userService.getExamList(stuNum);
            // 返回 200 状态码和考试信息
            System.out.println("获取考试信息成功");
            return ResponseEntity.ok(exam.toString());
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            System.out.println("获取考试信息失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
    public ResponseEntity<String> getUserProfile(@RequestParam String stuNum) {
        try {
            // 调用 UserService 的 getProfile 方法，返回一个字符串表示用户信息
            Profile profile = userService.getUserProfile(stuNum);
            // 返回 200 状态码和用户信息
            System.out.println("获取用户信息成功");
            return ResponseEntity.ok(profile.toString());
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            System.out.println("获取用户信息失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
