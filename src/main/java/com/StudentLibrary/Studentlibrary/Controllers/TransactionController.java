package com.StudentLibrary.Studentlibrary.Controllers;

import com.StudentLibrary.Studentlibrary.Model.Transaction;
import com.StudentLibrary.Studentlibrary.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    //what i need ideally is card_id and book_id

    @PostMapping("/issueBook")
    public ResponseEntity<?> issueBook(@RequestParam(value = "cardId") int cardId,
                                    @RequestParam("bookId")int bookId) throws Exception {
        String transaction_id=transactionService.issueBooks(cardId,bookId);
        return new ResponseEntity<>("Your Transaction was successfull here is your Txn id:"+transaction_id, HttpStatus.OK);
    }

    @PostMapping("/returnBook")
    public ResponseEntity<?> returnBook(@RequestParam("cardId") int cardId,
                                     @RequestParam("bookId") int bookId) throws Exception {
        String transaction_id=transactionService.returnBooks(cardId,bookId);
        return new ResponseEntity<>(
                "Your Transaction was Successful here is your Txn id:"+transaction_id,HttpStatus.OK);
    }

    @GetMapping("/transactions/history")
    public ResponseEntity<?> getBorrowingHistory(@RequestParam("studentId") int studentId) {
        try {
            return new ResponseEntity<>(transactionService.getBorrowingHistory(studentId), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/transactions/overdue")
    public ResponseEntity<?> getOverdueBooks() {
        List<Transaction> overdueList = transactionService.getOverdueBooks();
        if (overdueList.isEmpty()) {
            return new ResponseEntity<>("No overdue books found", HttpStatus.OK);
        }
        return new ResponseEntity<>(overdueList, HttpStatus.OK);
    }
}
