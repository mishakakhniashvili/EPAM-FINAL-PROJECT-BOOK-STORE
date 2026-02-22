package mytests.com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.BookStoreServiceSolutionApplication;
import com.epam.rd.autocode.spring.project.conf.SecurityConfig;
import com.epam.rd.autocode.spring.project.controller.ClientController;
import com.epam.rd.autocode.spring.project.service.ClientService;
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

@ContextConfiguration(classes = BookStoreServiceSolutionApplication.class)
@WebMvcTest(ClientController.class)
@Import(SecurityConfig.class)
class ClientControllerWebMvcTest {

    @Autowired MockMvc mvc;

    @MockBean ClientService clientService;

    private static final String VALID_CLIENT_JSON = """
        {
          "email": "c@mail.com",
          "password": "pass",
          "name": "Client",
          "balance": 100.00
        }
        """;

    @Test
    void getClients_unauth_returns401() throws Exception {
        mvc.perform(get("/clients"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getClients_employeeOk() throws Exception {
        when(clientService.getAllClients()).thenReturn(Collections.emptyList());

        mvc.perform(get("/clients")
                        .with(user("e@mail.com").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(clientService).getAllClients();
    }

    @Test
    void postClient_publicButNeedsCsrf() throws Exception {
        // no csrf => 403
        mvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CLIENT_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void postClient_withCsrf_ok() throws Exception {
        when(clientService.addClient(any())).thenReturn(null);

        mvc.perform(post("/clients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CLIENT_JSON))
                .andExpect(status().isOk());

        verify(clientService).addClient(any());
    }

    @Test
    void putClient_employeeOk() throws Exception {
        when(clientService.updateClientByEmail(any(), any())).thenReturn(null);

        mvc.perform(put("/clients/c@mail.com")
                        .with(user("e@mail.com").roles("EMPLOYEE"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CLIENT_JSON))
                .andExpect(status().isOk());

        verify(clientService).updateClientByEmail(eq("c@mail.com"), any());
    }
}