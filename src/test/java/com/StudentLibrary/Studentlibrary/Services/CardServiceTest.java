package com.StudentLibrary.Studentlibrary.Services;

import com.StudentLibrary.Studentlibrary.Model.Card;
import com.StudentLibrary.Studentlibrary.Model.CardStatus;
import com.StudentLibrary.Studentlibrary.Model.Student;
import com.StudentLibrary.Studentlibrary.Repositories.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCard_linksStudentAndCard_andSaves() {
        Student student = new Student();
        student.setName("Alice");

        // repository save returns the same entity in JPA by default; we don't depend on return value
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card result = cardService.createCard(student);

        assertNotNull(result, "Card should be created");
        assertSame(student, result.getStudent(), "Card should reference the provided student");
        assertSame(result, student.getCard(), "Student should be linked back to created card");
        assertEquals(CardStatus.ACTIVATED, result.getCardStatus(), "New card should be ACTIVATED by default");

        // verify save was called with the created card
        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository, times(1)).save(captor.capture());
        Card saved = captor.getValue();
        assertSame(result, saved, "Saved card should be the created instance");
    }

    @Test
    void createCard_propagatesRepositoryException() {
        Student student = new Student();
        doThrow(new RuntimeException("db error")).when(cardRepository).save(any(Card.class));
        assertThrows(RuntimeException.class, () -> cardService.createCard(student));
    }

    @Test
    void deactivate_callsRepositoryWithDeactivatedStatus() {
        int studentId = 10;

        cardService.deactivate(studentId);

        verify(cardRepository, times(1))
                .deactivateCard(eq(studentId), eq(CardStatus.DEACTIVATED.toString()));
    }

    @Test
    void createCard_doesNotOverwriteExistingStudentFields() {
        Student student = new Student();
        student.setName("Bob");
        student.setEmailId("bob@example.com");
        student.setAge(21);
        student.setCountry("US");

        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card card = cardService.createCard(student);

        assertEquals("Bob", student.getName());
        assertEquals("bob@example.com", student.getEmailId());
        assertEquals(21, student.getAge());
        assertEquals("US", student.getCountry());
        assertSame(card, student.getCard());
    }

    @Test
    void createCard_initializesBidirectionalLinkEvenIfStudentAlreadyHasCardNull() {
        Student student = new Student();
        student.setCard(null);

        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card card = cardService.createCard(student);

        assertNotNull(card);
        assertSame(card, student.getCard());
        assertSame(student, card.getStudent());
    }

    @Test
    void reactivate_callsRepositoryWithCorrectParams() {
        doNothing().when(cardRepository).reactivateCard(eq(2), anyString());

        cardService.reactivate(2);

        verify(cardRepository, times(1)).reactivateCard(eq(2), eq("ACTIVATED"));
    }

    @Test
    void getCardStatus_returnsStatus() {
        when(cardRepository.findCardStatus(3)).thenReturn("ACTIVATED");

        String status = cardService.getCardStatus(3);

        assertEquals("ACTIVATED", status);
        verify(cardRepository).findCardStatus(3);
    }

    @Test
    void testAutoDeactivateInactiveCards_deactivatesAndSaves() {
        // Arrange
        Card card1 = new Card();
        card1.setId(1);
        card1.setCardStatus(CardStatus.ACTIVATED);

        Card card2 = new Card();
        card2.setId(2);
        card2.setCardStatus(CardStatus.ACTIVATED);

        when(cardRepository.findInactiveCards(any(Date.class)))
                .thenReturn(List.of(card1, card2));

        // Act
        cardService.autoDeactivateInactiveCards();

        // Assert
        verify(cardRepository, times(2)).save(any(Card.class));
        assertEquals(CardStatus.DEACTIVATED, card1.getCardStatus());
        assertEquals(CardStatus.DEACTIVATED, card2.getCardStatus());
    }

    @Test
    void testAutoDeactivateInactiveCards_skipsEmptyList() {
        when(cardRepository.findInactiveCards(any(Date.class)))
                .thenReturn(Collections.emptyList());

        cardService.autoDeactivateInactiveCards();

        // Verify that no saves were attempted
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void testManuallyDeactivateInactiveCards_returnsCount() {
        Card card1 = new Card();
        card1.setId(1);
        card1.setCardStatus(CardStatus.ACTIVATED);

        Card card2 = new Card();
        card2.setId(2);
        card2.setCardStatus(CardStatus.ACTIVATED);

        when(cardRepository.findInactiveCards(any(Date.class)))
                .thenReturn(List.of(card1, card2));

        int count = cardService.manuallyDeactivateInactiveCards();

        verify(cardRepository, times(2)).save(any(Card.class));
        assertEquals(2, count);
        assertEquals(CardStatus.DEACTIVATED, card1.getCardStatus());
        assertEquals(CardStatus.DEACTIVATED, card2.getCardStatus());
    }

    @Test
    void testManuallyDeactivateInactiveCards_emptyList() {
        when(cardRepository.findInactiveCards(any(Date.class)))
                .thenReturn(Collections.emptyList());

        int count = cardService.manuallyDeactivateInactiveCards();

        verify(cardRepository, never()).save(any(Card.class));
        assertEquals(0, count);
    }
}
