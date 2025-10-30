package com.StudentLibrary.Studentlibrary.Services;

import com.StudentLibrary.Studentlibrary.Model.*;
import com.StudentLibrary.Studentlibrary.Repositories.BookRepository;
import com.StudentLibrary.Studentlibrary.Repositories.CardRepository;
import com.StudentLibrary.Studentlibrary.Repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    CardRepository cardRepository;
    @Value("${books.max_allowed}")
    int max_allowed_books;
    @Value("${books.max_allowed_days}")
    int max_days_allowed;
    @Value("${books.fine.per_day}")
    int fine_per_day;


    public String issueBooks(int cardId,int bookId) throws Exception {
        Book book=bookRepository.findById(bookId).get();
        System.out.println(book);

        if (book==null||book.isAvailable()!=true){
            throw new Exception("Book is either unavailable or not present!!");
        }
        Card card=cardRepository.findById(cardId).get();
        System.out.println(card);
        if (card==null||card.getCardStatus()== CardStatus.DEACTIVATED){
            throw new Exception("Card is invalid!!");
        }
        if (card.getBooks().size()>max_allowed_books){
            throw new Exception("Book limit reached for this card!!");
        }
        book.setAvailable(false);
        book.setCard(card);
        List<Book> books=card.getBooks();
        books.add(book);
        card.setBooks(books);
        bookRepository.updateBook(book);
        Transaction transaction=new Transaction();
        transaction.setCard(card);
        transaction.setBook(book);
        transaction.setIssueOperation(true);
        transaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        transactionRepository.save(transaction);
        return transaction.getTransactionId();
    }

    public String returnBooks(int cardId, int bookId) {
        List<Transaction> transactions = transactionRepository.findByCard_Book(cardId, bookId, TransactionStatus.SUCCESSFUL, true);
        Transaction last_issue_transaction = transactions.get(transactions.size() - 1);
        Date issueDate = last_issue_transaction.getTransactionDate();

        long issueTime = Math.abs(issueDate.getTime() - System.currentTimeMillis());
        long number_of_days_passed = TimeUnit.DAYS.convert(issueTime, TimeUnit.MILLISECONDS);

        int fine = 0;
        if (number_of_days_passed > max_days_allowed) {
            fine = (int) Math.abs(number_of_days_passed - max_days_allowed) * fine_per_day;
        }

        Card card = last_issue_transaction.getCard();
        Book book = last_issue_transaction.getBook();

        book.setCard(null);
        book.setAvailable(true);
        bookRepository.updateBook(book);

        // Add fine to the student
        if (fine > 0) {
            Student student = card.getStudent();
            student.setTotalFine(student.getTotalFine() + fine);
        }

        Transaction new_transaction = new Transaction();
        new_transaction.setBook(book);
        new_transaction.setCard(card);
        new_transaction.setFineAmount(fine);
        new_transaction.setIssueOperation(false);
        new_transaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        transactionRepository.save(new_transaction);

        return new_transaction.getTransactionId();
    }

    public List<Transaction> getBorrowingHistory(int studentId) {
        Card card = cardRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Card not found for student ID: " + studentId));
        return transactionRepository.findByCard(card);
    }

    public List<Transaction> getOverdueBooks() {
        Date now = new Date();
        List<Transaction> issuedTransactions = transactionRepository.findIssuedTransactions(TransactionStatus.SUCCESSFUL);
        return issuedTransactions.stream()
                .filter(t -> {
                    long days = TimeUnit.DAYS.convert(now.getTime() - t.getTransactionDate().getTime(), TimeUnit.MILLISECONDS);
                    return days > max_days_allowed;
                })
                .collect(Collectors.toList());
    }
}
