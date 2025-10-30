package com.StudentLibrary.Studentlibrary.Services;

import com.StudentLibrary.Studentlibrary.DTO.BookAnalyticsDTO;
import com.StudentLibrary.Studentlibrary.Model.Genre;
import com.StudentLibrary.Studentlibrary.Repositories.BookRepository;
import com.StudentLibrary.Studentlibrary.Repositories.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BookService bookService;

    @InjectMocks
    private TransactionService transactionService;


    @Test
    void testTopBorrowedBooks() {
        when(transactionRepository.findTopBorrowedBooks())
                .thenReturn(List.of(new BookAnalyticsDTO("Book A", 5L)));

        List<BookAnalyticsDTO> result = transactionService.getTopBorrowedBooks();

        assertEquals(1, result.size());
        assertEquals("Book A", result.get(0).getName());
        assertEquals(5L, result.get(0).getCount());
    }

    @Test
    void testTopBooksByGenre() {
        when(bookRepository.findTopBooksByGenre(any(Genre.class)))
                .thenReturn(List.of(new BookAnalyticsDTO("Fictional Novel", 7L)));

        List<BookAnalyticsDTO> result = bookService.getTopBooksByGenre(Genre.FICTIONAL);

        assertEquals("Fictional Novel", result.get(0).getName());
        assertEquals(7L, result.get(0).getCount());
    }

    @Test
    void testTopAuthors() {
        when(bookRepository.findTopAuthors())
                .thenReturn(List.of(new BookAnalyticsDTO("Jane Austen", 10L)));

        List<BookAnalyticsDTO> result = bookService.getTopAuthors();

        assertEquals("Jane Austen", result.get(0).getName());
        assertEquals(10L, result.get(0).getCount());
    }
}
