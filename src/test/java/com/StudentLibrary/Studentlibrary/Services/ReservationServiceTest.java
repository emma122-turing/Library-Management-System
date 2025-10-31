package com.StudentLibrary.Studentlibrary.Services;

import com.StudentLibrary.Studentlibrary.Model.*;
import com.StudentLibrary.Studentlibrary.Repositories.BookRepository;
import com.StudentLibrary.Studentlibrary.Repositories.ReservationRepository;
import com.StudentLibrary.Studentlibrary.Repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Book unavailableBook;
    private Student student;

    @BeforeEach
    void setup() {
        unavailableBook = new Book();
        unavailableBook.setId(1);
        unavailableBook.setAvailable(false);

        student = new Student();
        student.setId(10);
        student.setName("John Doe");
    }

    @Test
    void reserveBook_shouldCreatePendingReservation_whenBookUnavailable() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(unavailableBook));
        when(studentRepository.findById(10)).thenReturn(Optional.of(student));

        reservationService.reserveBook(1, 10);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void reserveBook_shouldThrow_whenBookAvailable() {
        Book availableBook = new Book();
        availableBook.setAvailable(true);
        when(bookRepository.findById(1)).thenReturn(Optional.of(availableBook));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                reservationService.reserveBook(1, 10));

        assertEquals("Book is available — no need to reserve.", ex.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserveBook_shouldThrow_whenBookNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                reservationService.reserveBook(1, 10));

        assertEquals("Book not found", ex.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void getReservationsByStudent_shouldReturnList() {
        when(reservationRepository.findByStudentId(10))
                .thenReturn(List.of(new Reservation(), new Reservation()));

        List<Reservation> result = reservationService.getReservationsByStudent(10);

        assertEquals(2, result.size());
    }

    @Test
    void handleBookReturn_shouldFulfillNextPendingReservation() {
        Reservation res1 = new Reservation();
        res1.setId(101);
        res1.setStatus(ReservationStatus.PENDING);

        List<Reservation> queue = new ArrayList<>();
        queue.add(res1);

        when(reservationRepository.findByBookAndStatusOrderByReservationDateAsc(
                any(Book.class), eq(ReservationStatus.PENDING)))
                .thenReturn(queue);

        reservationService.handleBookReturn(unavailableBook);

        assertEquals(ReservationStatus.FULFILLED, res1.getStatus());
        verify(reservationRepository, times(1)).save(res1);
    }

    @Test
    void handleBookReturn_shouldDoNothingIfNoPendingReservations() {
        when(reservationRepository.findByBookAndStatusOrderByReservationDateAsc(
                any(Book.class), eq(ReservationStatus.PENDING)))
                .thenReturn(Collections.emptyList());

        reservationService.handleBookReturn(unavailableBook);

        verify(reservationRepository, never()).save(any());
    }
}
