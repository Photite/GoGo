package cn.edu.hbwe.gogo.controller;

import cn.edu.hbwe.gogo.entity.ClassUnit;
import cn.edu.hbwe.gogo.entity.SchoolCalender;
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

    // 定义一个登录的请求，接收学号和密码
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String stuNum, @RequestParam String password) {
        try {
            // 调用 UserService 的 login 方法，返回一个布尔值表示是否登录成功
            boolean result = userService.login(stuNum, password);
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
    @GetMapping("/timetable")
//    public ResponseEntity<String> getTimetable(@RequestParam int year, @RequestParam int term) {
    public ResponseEntity<String> getTimetable(@RequestParam String stuNum) {
        try {
            // 调用 UserService 的 getTimetable 方法，返回一个字符串表示课表内容
//            String timetable = userService.getTimetable(year, term);
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

    // 定义一个查询学校日期的请求
    @GetMapping("/schoolCalender")
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
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
