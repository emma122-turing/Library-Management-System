package com.StudentLibrary.Studentlibrary.Controllers;

import com.StudentLibrary.Studentlibrary.Model.Reservation;
import com.StudentLibrary.Studentlibrary.Services.ReservationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Test
    void reserveBook_shouldReturnCreatedStatus() throws Exception {
        Mockito.doNothing().when(reservationService).reserveBook(1, 10);

        mockMvc.perform(post("/books/reservations")
                        .param("bookId", "1")
                        .param("studentId", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("Book reserved successfully"));

        verify(reservationService, times(1)).reserveBook(1, 10);
    }

    @Test
    void getStudentReservations_shouldReturnList() throws Exception {
        when(reservationService.getReservationsByStudent(anyInt()))
                .thenReturn(List.of(new Reservation(), new Reservation()));

        mockMvc.perform(get("/books/reservations")
                        .param("studentId", "10"))
                .andExpect(status().isOk());

        verify(reservationService, times(1)).getReservationsByStudent(10);
    }
}
