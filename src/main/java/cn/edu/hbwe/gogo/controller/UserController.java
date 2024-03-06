package cn.edu.hbwe.gogo.controller;

import cn.edu.hbwe.gogo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> getTimetable(@RequestParam int year, @RequestParam int term) {
        try {
            // 调用 UserService 的 getTimetable 方法，返回一个字符串表示课表内容
            String timetable = userService.getTimetable(year, term);
            // 返回 200 状态码和课表内容
            return ResponseEntity.ok(timetable);
        } catch (Exception e) {
            // 如果发生异常，返回 500 状态码和异常信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        boolean result = userService.logout();
        if (result) {
            return ResponseEntity.ok("退出成功");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("退出失败");
        }
    }
}
