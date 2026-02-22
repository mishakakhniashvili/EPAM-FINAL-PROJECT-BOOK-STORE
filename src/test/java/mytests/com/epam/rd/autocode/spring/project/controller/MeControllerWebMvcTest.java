package mytests.com.epam.rd.autocode.spring.project.controller;
import com.epam.rd.autocode.spring.project.BookStoreServiceSolutionApplication;
import com.epam.rd.autocode.spring.project.conf.SecurityConfig;
import com.epam.rd.autocode.spring.project.controller.MeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(MeController.class)
@Import(SecurityConfig.class)
@ContextConfiguration(classes = BookStoreServiceSolutionApplication.class)
class MeControllerWebMvcTest {

    @Autowired MockMvc mvc;

    @Test
    void me_authenticated_returnsUsernameAndRoles() throws Exception {
        mvc.perform(get("/me")
                        .with(user("e@mail.com").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("e@mail.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_EMPLOYEE"));
    }
}