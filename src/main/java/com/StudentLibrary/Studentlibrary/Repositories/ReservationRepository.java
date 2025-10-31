package com.StudentLibrary.Studentlibrary.Repositories;

import com.StudentLibrary.Studentlibrary.Model.Book;
import com.StudentLibrary.Studentlibrary.Model.Reservation;
import com.StudentLibrary.Studentlibrary.Model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByBookAndStatusOrderByReservationDateAsc(Book book, ReservationStatus status);

    List<Reservation> findByStudentId(int studentId);
}
