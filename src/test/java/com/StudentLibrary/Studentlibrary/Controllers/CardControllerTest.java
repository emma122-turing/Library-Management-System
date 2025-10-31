package com.StudentLibrary.Studentlibrary.Controllers;

import com.StudentLibrary.Studentlibrary.Services.CardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;


@WebMvcTest(controllers = CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Test
    void deactivateCard_success() throws Exception {
        int studentId = 10;
        Mockito.doNothing().when(cardService).deactivate(eq(studentId));

        mockMvc.perform(put("/cards/deactivate").param("studentId", String.valueOf(studentId)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Card deactivated successfully"));

        Mockito.verify(cardService).deactivate(eq(studentId));
    }

    @Test
    void reactivateCard_success() throws Exception {
        int studentId = 5;
        Mockito.doNothing().when(cardService).reactivate(eq(studentId));

        mockMvc.perform(put("/cards/reactivate").param("studentId", String.valueOf(studentId)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Card reactivated successfully"));

        Mockito.verify(cardService).reactivate(eq(studentId));
    }

    @Test
    void getCardStatus_success() throws Exception {
        int studentId = 3;
        when(cardService.getCardStatus(eq(studentId))).thenReturn("ACTIVE");

        mockMvc.perform(get("/cards/status").param("studentId", String.valueOf(studentId)))
                .andExpect(status().isOk())
                .andExpect(content().string("ACTIVE"));
    }

    @Test
    void deactivateCard_missingParam_shouldFail() throws Exception {
        mockMvc.perform(put("/cards/deactivate"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCardStatus_propagatesServiceResult() throws Exception {
        int studentId = 42;
        when(cardService.getCardStatus(eq(studentId))).thenReturn("INACTIVE");

        mockMvc.perform(get("/cards/status").param("studentId", String.valueOf(studentId)))
                .andExpect(status().isOk())
                .andExpect(content().string("INACTIVE"));
    }

    @Test
    void manuallyDeactivateInactiveCards_success() throws Exception {
        when(cardService.manuallyDeactivateInactiveCards()).thenReturn(4);

        mockMvc.perform(post("/cards/deactivate/inactive"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Manual deactivation completed: 4 card(s) deactivated."));

        verify(cardService, times(1)).manuallyDeactivateInactiveCards();
    }

    @Test
    void manuallyDeactivateInactiveCards_zeroCards() throws Exception {
        when(cardService.manuallyDeactivateInactiveCards()).thenReturn(0);

        mockMvc.perform(post("/cards/deactivate/inactive"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Manual deactivation completed: 0 card(s) deactivated."));

        verify(cardService, times(1)).manuallyDeactivateInactiveCards();
    }
}
