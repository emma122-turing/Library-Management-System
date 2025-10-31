package com.StudentLibrary.Studentlibrary.Services;

import com.StudentLibrary.Studentlibrary.Model.Book;
import com.StudentLibrary.Studentlibrary.Model.Reservation;
import com.StudentLibrary.Studentlibrary.Model.ReservationStatus;
import com.StudentLibrary.Studentlibrary.Model.Student;
import com.StudentLibrary.Studentlibrary.Repositories.BookRepository;
import com.StudentLibrary.Studentlibrary.Repositories.ReservationRepository;
import com.StudentLibrary.Studentlibrary.Repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    public void reserveBook(int bookId, int studentId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.isAvailable()) {
            throw new RuntimeException("Book is available — no need to reserve.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setStudent(student);
        reservation.setReservationDate(new Date());
        reservation.setStatus(ReservationStatus.PENDING);
        reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByStudent(int studentId) {
        return reservationRepository.findByStudentId(studentId);
    }

    public void handleBookReturn(Book book) {
        List<Reservation> queue = reservationRepository.findByBookAndStatusOrderByReservationDateAsc(book, ReservationStatus.PENDING);
        if (!queue.isEmpty()) {
            Reservation next = queue.get(0);
            next.setStatus(ReservationStatus.FULFILLED);
            reservationRepository.save(next);
        }
    }
}
