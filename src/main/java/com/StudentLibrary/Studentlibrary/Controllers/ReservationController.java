package com.StudentLibrary.Studentlibrary.Controllers;

import com.StudentLibrary.Studentlibrary.Model.Reservation;
import com.StudentLibrary.Studentlibrary.Services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<String> reserveBook(@RequestParam int bookId, @RequestParam int studentId) {
        reservationService.reserveBook(bookId, studentId);
        return new ResponseEntity<>("Book reserved successfully", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getStudentReservations(@RequestParam int studentId) {
        return new ResponseEntity<>(reservationService.getReservationsByStudent(studentId), HttpStatus.OK);
    }
}
