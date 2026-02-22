package mytests.com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.BookStoreServiceSolutionApplication;
import com.epam.rd.autocode.spring.project.conf.SecurityConfig;
import com.epam.rd.autocode.spring.project.controller.BookController;
import com.epam.rd.autocode.spring.project.service.BookService;
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
@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
class BookControllerWebMvcTest {

    @Autowired MockMvc mvc;

    @MockBean BookService bookService;

    private static final String VALID_BOOK_JSON = """
        {
          "name": "Test Book",
          "author": "Test Author",
          "genre": "Test Genre",
          "price": 10.50,
          "publicationDate": "2020-01-01",
          "pages": 123,
          "language": "ENGLISH",
          "ageGroup": "ADULT",
          "characteristics": "chars",
          "description": "desc"
        }
        """;

    @Test
    void getBooks_isPublic_returns200() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookService).getAllBooks();
    }

    @Test
    void postBook_clientForbidden() throws Exception {
        mvc.perform(post("/books")
                        .with(user("c@mail.com").roles("CLIENT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BOOK_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(bookService);
    }

    @Test
    void postBook_employeeOk() throws Exception {
        when(bookService.addBook(any())).thenReturn(null);

        mvc.perform(post("/books")
                        .with(user("e@mail.com").roles("EMPLOYEE"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BOOK_JSON))
                .andExpect(status().isOk());

        verify(bookService).addBook(any());
    }
}