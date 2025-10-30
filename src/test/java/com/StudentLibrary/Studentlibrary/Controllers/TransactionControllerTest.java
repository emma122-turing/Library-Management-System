package com.StudentLibrary.Studentlibrary.Controllers;

import com.StudentLibrary.Studentlibrary.Model.Transaction;
import com.StudentLibrary.Studentlibrary.Services.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void issueBook_success() throws Exception {
        int cardId = 1;
        int bookId = 2;
        when(transactionService.issueBooks(eq(cardId), eq(bookId))).thenReturn("TXN123");

        mockMvc.perform(post("/issueBook")
                        .param("cardId", String.valueOf(cardId))
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().isOk())
                .andExpect(content().string("Your Transaction was successfull here is your Txn id:TXN123"));
    }

    @Test
    void returnBook_success() throws Exception {
        int cardId = 3;
        int bookId = 4;
        when(transactionService.returnBooks(eq(cardId), eq(bookId))).thenReturn("TXN456");

        mockMvc.perform(post("/returnBook")
                        .param("cardId", String.valueOf(cardId))
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().isOk())
                .andExpect(content().string("Your Transaction was Successful here is your Txn id:TXN456"));
    }

    @Test
    void getBorrowingHistory_success() throws Exception {
        int studentId = 7;
        List<Transaction> history = List.of(new Transaction(), new Transaction());
        when(transactionService.getBorrowingHistory(eq(studentId))).thenReturn(history);

        mockMvc.perform(get("/transactions/history").param("studentId", String.valueOf(studentId)))
                .andExpect(status().isOk());
    }

    @Test
    void getBorrowingHistory_notFound() throws Exception {
        int studentId = 99;
        when(transactionService.getBorrowingHistory(eq(studentId)))
                .thenThrow(new RuntimeException("Student not found"));

        mockMvc.perform(get("/transactions/history").param("studentId", String.valueOf(studentId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student not found"));
    }

    @Test
    void getOverdueBooks_emptyList_returnsMessage() throws Exception {
        when(transactionService.getOverdueBooks()).thenReturn(List.of());

        mockMvc.perform(get("/transactions/overdue"))
                .andExpect(status().isOk())
                .andExpect(content().string("No overdue books found"));
    }

    @Test
    void getOverdueBooks_nonEmpty_returnsList() throws Exception {
        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();
        when(transactionService.getOverdueBooks()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/transactions/overdue").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void issueBook_missingParams_shouldFail() throws Exception {
        mockMvc.perform(post("/issueBook").param("cardId", "1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/issueBook").param("bookId", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnBook_missingParams_shouldFail() throws Exception {
        mockMvc.perform(post("/returnBook").param("cardId", "1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/returnBook").param("bookId", "2"))
                .andExpect(status().isBadRequest());
    }
}
