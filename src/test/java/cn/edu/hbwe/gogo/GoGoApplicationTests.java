package cn.edu.hbwe.gogo;

import cn.edu.hbwe.gogo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GoGoApplicationTests {

    @Test
    void contextLoads() {
    }

//    @Test
//    void testLoginAndFetchTimetable() throws Exception {
//        String stuNum = "1008520090615";
//        String password = "Linhao521";
//        UserService userService = new UserService(stuNum, password);
//        userService.init();
//        boolean loginResult = userService.beginLogin();
//        assertTrue(loginResult, "Login failed");
//
//        System.out.println("---查询课表---");
//        System.out.print("输入学年（2018-2019就输2018）:");
//        int year = 2023;
//        System.out.print("输入学期（1或2）:");
//        int term = 1;
//        userService.getStudentTimetable(year, term);
//
//        userService.logout();
//    }


}
