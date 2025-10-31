package com.StudentLibrary.Studentlibrary.Services;

import com.StudentLibrary.Studentlibrary.Model.*;
import com.StudentLibrary.Studentlibrary.Repositories.BookRepository;
import com.StudentLibrary.Studentlibrary.Repositories.CardRepository;
import com.StudentLibrary.Studentlibrary.Repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    TransactionRepository transactionRepository;
    @Mock
    BookRepository bookRepository;
    @Mock
    CardRepository cardRepository;

    @InjectMocks
    TransactionService transactionService;

    @Mock
    ReservationService reservationService;

    private Book book;
    private Card card;
    private Student student;

    @BeforeEach
    void setUp() {
        // Inject configuration values via reflection since @Value won't resolve in unit test
        setField(transactionService, "max_allowed_books", 3);
        setField(transactionService, "max_days_allowed", 7);
        setField(transactionService, "fine_per_day", 10);

        student = new Student();
        student.setId(1);
        student.setName("Alice");
        student.setTotalFine(0);

        card = new Card();
        card.setId(11);
        card.setCardStatus(CardStatus.ACTIVATED);
        card.setBooks(new ArrayList<>());
        card.setStudent(student);

        book = new Book();
        book.setId(101);
        book.setAvailable(true);
    }

    // Utility to set private fields
    private static void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field f = TransactionService.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void returnBooks_calculatesFineWhenOverdueAndCreatesTransaction() {
        // 10 days old transaction
        Transaction issueTx = new Transaction();
        issueTx.setTransactionDate(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10)));
        issueTx.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        issueTx.setIssueOperation(true);
        issueTx.setCard(card);
        issueTx.setBook(book);

        when(transactionRepository.findByCard_Book(eq(11), eq(101),
                eq(TransactionStatus.SUCCESSFUL), eq(true)))
                .thenReturn(List.of(issueTx));

        String id = transactionService.returnBooks(11, 101);

        assertNotNull(id);
        assertTrue(book.isAvailable());
        assertNull(book.getCard());
        assertEquals(30, student.getTotalFine()); // 10 - 7 = 3 days * 10 = 30
        verify(bookRepository).updateBook(book);
        verify(transactionRepository).save(any(Transaction.class));
        verify(reservationService).handleBookReturn(book);
    }

    @Test
    void returnBooks_noFineWhenWithinLimit() {
        // 3 days old transaction
        Transaction issueTx = new Transaction();
        issueTx.setTransactionDate(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)));
        issueTx.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        issueTx.setIssueOperation(true);
        issueTx.setCard(card);
        issueTx.setBook(book);

        when(transactionRepository.findByCard_Book(eq(11), eq(101),
                eq(TransactionStatus.SUCCESSFUL), eq(true)))
                .thenReturn(List.of(issueTx));

        String id = transactionService.returnBooks(11, 101);

        assertNotNull(id);
        assertEquals(0, student.getTotalFine()); // no fine
        assertTrue(book.isAvailable());
        verify(bookRepository).updateBook(book);
        verify(transactionRepository).save(any(Transaction.class));
        verify(reservationService).handleBookReturn(book);
    }

    @Test
    void getBorrowingHistory_returnsTransactionsForStudentCard() {
        when(cardRepository.findByStudentId(1)).thenReturn(Optional.of(card));
        List<Transaction> list = List.of(new Transaction(), new Transaction());
        when(transactionRepository.findByCard(card)).thenReturn(list);

        List<Transaction> result = transactionService.getBorrowingHistory(1);

        assertEquals(2, result.size());
        verify(cardRepository).findByStudentId(1);
        verify(transactionRepository).findByCard(card);
    }

    @Test
    void getOverdueBooks_filtersByMaxDaysAllowed() {
        // Create two issued transactions: one overdue, one not
        Transaction overdue = new Transaction();
        overdue.setTransactionDate(new Date(System.currentTimeMillis() - 9L * 24 * 60 * 60 * 1000));
        overdue.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        overdue.setIssueOperation(true);

        Transaction notOverdue = new Transaction();
        notOverdue.setTransactionDate(new Date(System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000));
        notOverdue.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        notOverdue.setIssueOperation(true);

        when(transactionRepository.findIssuedTransactions(TransactionStatus.SUCCESSFUL))
                .thenReturn(List.of(overdue, notOverdue));

        List<Transaction> result = transactionService.getOverdueBooks();

        assertEquals(1, result.size());
        assertTrue(result.contains(overdue));
        assertFalse(result.contains(notOverdue));
        verify(transactionRepository).findIssuedTransactions(TransactionStatus.SUCCESSFUL);
    }

    @Test
    void getBorrowingHistory_returnsTransactions() {
        int studentId = 10;
        Card mockCard = new Card();
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());

        when(cardRepository.findByStudentId(studentId)).thenReturn(Optional.of(mockCard));
        when(transactionRepository.findByCard(mockCard)).thenReturn(transactions);

        List<Transaction> result = transactionService.getBorrowingHistory(studentId);

        assertEquals(2, result.size());
        verify(cardRepository).findByStudentId(studentId);
        verify(transactionRepository).findByCard(mockCard);
    }

    @Test
    void getBorrowingHistory_cardNotFound() {
        when(cardRepository.findByStudentId(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> transactionService.getBorrowingHistory(99));
    }

    @Test
    void getOverdueBooks_returnsOverdueList() {
        Transaction oldTxn = new Transaction();
        oldTxn.setTransactionDate(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(20)));
        oldTxn.setIssueOperation(true);
        oldTxn.setTransactionStatus(TransactionStatus.SUCCESSFUL);

        when(transactionRepository.findIssuedTransactions(TransactionStatus.SUCCESSFUL))
                .thenReturn(List.of(oldTxn));

        transactionService.max_days_allowed = 7;

        List<Transaction> result = transactionService.getOverdueBooks();
        assertEquals(1, result.size());
    }

    @Test
    void getOverdueBooks_noOverdue() {
        Transaction recentTxn = new Transaction();
        recentTxn.setTransactionDate(new Date());
        recentTxn.setIssueOperation(true);
        recentTxn.setTransactionStatus(TransactionStatus.SUCCESSFUL);

        when(transactionRepository.findIssuedTransactions(TransactionStatus.SUCCESSFUL))
                .thenReturn(List.of(recentTxn));

        transactionService.max_days_allowed = 7;

        List<Transaction> result = transactionService.getOverdueBooks();
        assertTrue(result.isEmpty());
    }
}
