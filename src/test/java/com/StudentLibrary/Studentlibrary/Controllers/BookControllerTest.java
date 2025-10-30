package com.StudentLibrary.Studentlibrary.Controllers;

import com.StudentLibrary.Studentlibrary.DTO.BookAnalyticsDTO;
import com.StudentLibrary.Studentlibrary.Model.Genre;
import com.StudentLibrary.Studentlibrary.Services.BookService;
import com.StudentLibrary.Studentlibrary.Services.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private TransactionService transactionService;

    @Test
    void getPopularBooks_returnsList() throws Exception {
        List<BookAnalyticsDTO> mockList = List.of(
                new BookAnalyticsDTO("Book A", 5L),
                new BookAnalyticsDTO("Book B", 3L)
        );

        Mockito.when(transactionService.getTopBorrowedBooks()).thenReturn(mockList);

        mockMvc.perform(get("/books/popular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Book A"))
                .andExpect(jsonPath("$[0].count").value(5))
                .andExpect(jsonPath("$[1].name").value("Book B"))
                .andExpect(jsonPath("$[1].count").value(3));
    }

    @Test
    void getPopularBooksByGenre_returnsList() throws Exception {
        List<BookAnalyticsDTO> mockList = List.of(
                new BookAnalyticsDTO("Fictional Novel", 7L)
        );

        Mockito.when(bookService.getTopBooksByGenre(any(Genre.class))).thenReturn(mockList);

        mockMvc.perform(get("/books/genre/top")
                        .param("genre", "FICTIONAL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fictional Novel"))
                .andExpect(jsonPath("$[0].count").value(7));
    }

    @Test
    void getTopAuthors_returnsList() throws Exception {
        List<BookAnalyticsDTO> mockList = List.of(
                new BookAnalyticsDTO("Jane Austen", 10L),
                new BookAnalyticsDTO("Mark Twain", 8L)
        );

        Mockito.when(bookService.getTopAuthors()).thenReturn(mockList);

        mockMvc.perform(get("/books/authors/top")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Jane Austen"))
                .andExpect(jsonPath("$[0].count").value(10))
                .andExpect(jsonPath("$[1].name").value("Mark Twain"))
                .andExpect(jsonPath("$[1].count").value(8));
    }
}
