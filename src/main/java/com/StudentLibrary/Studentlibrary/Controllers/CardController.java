package com.StudentLibrary.Studentlibrary.Controllers;

import com.StudentLibrary.Studentlibrary.Services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @PutMapping("/deactivate")
    public ResponseEntity<String> deactivateCard(@RequestParam int studentId) {
        cardService.deactivate(studentId);
        return new ResponseEntity<>("Card deactivated successfully", HttpStatus.ACCEPTED);
    }

    @PutMapping("/reactivate")
    public ResponseEntity<String> reactivateCard(@RequestParam int studentId) {
        cardService.reactivate(studentId);
        return new ResponseEntity<>("Card reactivated successfully", HttpStatus.ACCEPTED);
    }

    @GetMapping("/status")
    public ResponseEntity<String> getCardStatus(@RequestParam int studentId) {
        String status = cardService.getCardStatus(studentId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
