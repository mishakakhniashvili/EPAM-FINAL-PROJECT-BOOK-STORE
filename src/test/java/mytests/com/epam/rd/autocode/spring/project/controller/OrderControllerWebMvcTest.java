package mytests.com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.BookStoreServiceSolutionApplication;
import com.epam.rd.autocode.spring.project.conf.SecurityConfig;
import com.epam.rd.autocode.spring.project.controller.OrderController;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
@ContextConfiguration(classes = BookStoreServiceSolutionApplication.class)
class OrderControllerWebMvcTest {

    @Autowired MockMvc mvc;

    @MockBean OrderRepository orderRepository;
    @MockBean OrderService orderService;
    @MockBean EmployeeRepository employeeRepository;
    @MockBean Validator validator;

    private static final String VALID_ORDER_JSON = """
        {
          "orderDate": "2024-01-01T10:00:00",
          "bookItems": [
            { "bookName": "Test Book", "quantity": 1 }
          ]
        }
        """;

    @Test
    void myOrders_client_callsServiceByClient() throws Exception {
        when(orderService.getOrdersByClient("c@mail.com")).thenReturn(Collections.emptyList());

        mvc.perform(get("/orders/my")
                        .with(user("c@mail.com").roles("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(orderService).getOrdersByClient("c@mail.com");
    }


    @Test
    void addOrder_client_autofillsEmployee_andOk() throws Exception {
        when(employeeRepository.findEmployeeEmailsOrderByLoad()).thenReturn(List.of("emp@mail.com"));
        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(orderService.addOrder(any())).thenReturn(null);

        mvc.perform(post("/orders")
                        .with(user("c@mail.com").roles("CLIENT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORDER_JSON))
                .andExpect(status().isOk());

        verify(orderService).addOrder(any());
    }
}