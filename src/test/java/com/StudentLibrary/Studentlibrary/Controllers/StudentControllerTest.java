package com.StudentLibrary.Studentlibrary.Controllers;

import com.StudentLibrary.Studentlibrary.Services.StudentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    void getFine_success() throws Exception {
        int studentId = 3;
        when(studentService.getFine(eq(studentId))).thenReturn(42.5);

        mockMvc.perform(get("/students/fine/check").param("studentId", String.valueOf(studentId)))
                .andExpect(status().isOk())
                .andExpect(content().string("42.5"));
    }

    @Test
    void clearFine_success() throws Exception {
        int studentId = 7;
        Mockito.doNothing().when(studentService).clearFine(eq(studentId));

        mockMvc.perform(put("/students/fine/clear").param("studentId", String.valueOf(studentId)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Fine cleared successfully"));

        Mockito.verify(studentService).clearFine(eq(studentId));
    }

    @Test
    void getFine_missingParam_shouldFail() throws Exception {
        mockMvc.perform(get("/students/fine/check"))
                .andExpect(status().isBadRequest());
    }
}
