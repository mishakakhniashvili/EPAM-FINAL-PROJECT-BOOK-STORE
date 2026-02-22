package mytests.com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.BookStoreServiceSolutionApplication;
import com.epam.rd.autocode.spring.project.conf.SecurityConfig;
import com.epam.rd.autocode.spring.project.controller.EmployeeController;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(SecurityConfig.class)
@ContextConfiguration(classes = BookStoreServiceSolutionApplication.class)
class EmployeeControllerWebMvcTest {

    @Autowired MockMvc mvc;

    @MockBean EmployeeService employeeService;

    private static final String VALID_EMPLOYEE_JSON = """
        {
          "email": "e@mail.com",
          "password": "pass",
          "name": "Emp",
          "birthDate": "2000-01-01",
          "phone": "+995000000000"
        }
        """;

    @Test
    void getEmployees_clientForbidden() throws Exception {
        mvc.perform(get("/employees")
                        .with(user("c@mail.com").roles("CLIENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getEmployees_employeeOk() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        mvc.perform(get("/employees")
                        .with(user("e@mail.com").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(employeeService).getAllEmployees();
    }

    @Test
    void postEmployee_employeeOk() throws Exception {
        when(employeeService.addEmployee(any())).thenReturn(null);

        mvc.perform(post("/employees")
                        .with(user("e@mail.com").roles("EMPLOYEE"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_EMPLOYEE_JSON))
                .andExpect(status().isOk());

        verify(employeeService).addEmployee(any());
    }
}