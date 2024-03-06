package cn.edu.hbwe.gogo;

import cn.edu.hbwe.gogo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GoGoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("stuNum", "1008520090615")
                        .param("password", "Linhao521"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTimetable() throws Exception {
        mockMvc.perform(post("/getTimetable")
                        .param("year", "2023")
                        .param("term", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isOk());
    }

}
