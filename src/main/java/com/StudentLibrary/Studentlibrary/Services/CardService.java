package com.StudentLibrary.Studentlibrary.Services;

import com.StudentLibrary.Studentlibrary.Model.Card;
import com.StudentLibrary.Studentlibrary.Model.CardStatus;
import com.StudentLibrary.Studentlibrary.Model.Student;
import com.StudentLibrary.Studentlibrary.Repositories.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class CardService {

    @Autowired
    CardRepository cardRepository;

    Logger logger= LoggerFactory.getLogger(CardService.class);

    @Value("${cards.inactivity.days:90}")
    private int inactivityDays;

    public Card createCard(Student student){
        Card card =new Card();
        student.setCard(card);
        card.setStudent(student);
        cardRepository.save(card);
        return card;
    }

    @Transactional
    public void deactivate(int student_id){
        cardRepository.deactivateCard(student_id, CardStatus.DEACTIVATED.toString());
    }

    @Transactional
    public void reactivate(int studentId) {
        cardRepository.reactivateCard(studentId, CardStatus.ACTIVATED.toString());
    }

    public String getCardStatus(int studentId) {
        return cardRepository.findCardStatus(studentId);
    }

    /**
     * Scheduled method that runs daily at midnight.
     * Automatically deactivates cards that have been inactive for a configurable number of days.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void autoDeactivateInactiveCards() {
        Date thresholdDate = Date.from(
                LocalDate.now()
                        .minusDays(inactivityDays)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );

        List<Card> inactiveCards = cardRepository.findInactiveCards(thresholdDate);

        if (inactiveCards.isEmpty()) {
            logger.info("No inactive cards found for auto-deactivation (threshold: {} days).", inactivityDays);
            return;
        }

        for (Card card : inactiveCards) {
            card.setCardStatus(CardStatus.DEACTIVATED);
            cardRepository.save(card);
        }

        logger.info("Auto-deactivated {} inactive cards (inactive > {} days).",
                inactiveCards.size(), inactivityDays);
    }

    /**
     * Manual admin trigger — allows manual execution of the same deactivation process.
     * @return number of cards deactivated in this run
     */
    public int manuallyDeactivateInactiveCards() {
        Date thresholdDate = Date.from(
                LocalDate.now()
                        .minusDays(inactivityDays)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );

        List<Card> inactiveCards = cardRepository.findInactiveCards(thresholdDate);
        for (Card card : inactiveCards) {
            card.setCardStatus(CardStatus.DEACTIVATED);
            cardRepository.save(card);
        }

        logger.info("Manual deactivation completed: {} cards deactivated.", inactiveCards.size());
        return inactiveCards.size();
    }
}
