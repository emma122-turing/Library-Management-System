package com.StudentLibrary.Studentlibrary.Services;

import com.StudentLibrary.Studentlibrary.Model.Card;
import com.StudentLibrary.Studentlibrary.Model.Student;
import com.StudentLibrary.Studentlibrary.Repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1);
        student.setName("Alice");
        student.setEmailId("alice@example.com"); // corrected
    }

    @Test
    void createStudent_createsCardAndLogs() {
        Card card = new Card();
        when(cardService.createCard(student)).thenReturn(card);

        assertDoesNotThrow(() -> studentService.createStudent(student));

        verify(cardService, times(1)).createCard(student);
        // No repository save is called directly by service per current implementation
        verifyNoInteractions(studentRepository);
    }

    @Test
    void createStudent_propagatesExceptionFromCardService() {
        when(cardService.createCard(student)).thenThrow(new RuntimeException("card error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> studentService.createStudent(student));
        assertEquals("card error", ex.getMessage());

        verify(cardService, times(1)).createCard(student);
        verifyNoInteractions(studentRepository);
    }

    @Test
    void updateStudent_updatesThroughRepository() {
        when(studentRepository.updateStudentDetails(student)).thenReturn(1);

        int updated = studentService.updateStudent(student);

        assertEquals(1, updated);
        verify(studentRepository, times(1)).updateStudentDetails(student);
        verifyNoInteractions(cardService);
    }

    @Test
    void updateStudent_returnsZeroWhenNoChange() {
        when(studentRepository.updateStudentDetails(student)).thenReturn(0);

        int updated = studentService.updateStudent(student);

        assertEquals(0, updated);
        verify(studentRepository, times(1)).updateStudentDetails(student);
        verifyNoInteractions(cardService);
    }

    @Test
    void deleteStudent_deactivatesCardThenDeletes() {
        doNothing().when(cardService).deactivate(1);
        when(studentRepository.deleteCustom(1)).thenReturn(1);

        studentService.deleteStudent(1);

        InOrder inOrder = inOrder(cardService, studentRepository);
        inOrder.verify(cardService).deactivate(1);
        inOrder.verify(studentRepository).deleteCustom(1);
    }

    @Test
    void getFine_returnsFineValue() {
        Student student = new Student();
        student.setId(1);
        student.setTotalFine(120);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

        double fine = studentService.getFine(1);

        assertEquals(120, fine);
        verify(studentRepository).findById(1);
    }

    @Test
    void clearFine_resetsFine() {
        Student student = new Student();
        student.setId(2);
        student.setTotalFine(200);
        when(studentRepository.findById(2)).thenReturn(Optional.of(student));

        studentService.clearFine(2);

        assertEquals(0, student.getTotalFine());
        verify(studentRepository).save(student);
    }
}