package com.StudentLibrary.Studentlibrary.Repositories;

import com.StudentLibrary.Studentlibrary.DTO.BookAnalyticsDTO;
import com.StudentLibrary.Studentlibrary.Model.Card;
import com.StudentLibrary.Studentlibrary.Model.Transaction;
import com.StudentLibrary.Studentlibrary.Model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {


    @Query("select t from Transaction t where t.card.id=:card_id and t.book.id=:book_id and t.transactionStatus=:status and t.isIssueOperation=:isIssue")
    public List<Transaction> findByCard_Book(@Param("card_id") int card_id,
                                            @Param("book_id") int book_id,
                                            @Param("status") TransactionStatus status,
                                            @Param("isIssue") boolean isIssue);


    @Query("SELECT t FROM Transaction t WHERE t.transactionStatus = :status AND t.isIssueOperation = true")
    List<Transaction> findIssuedTransactions(@Param("status") TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE t.card = :card ORDER BY t.transactionDate DESC")
    List<Transaction> findByCard(@Param("card") Card card);

    @Query("SELECT new com.StudentLibrary.Studentlibrary.DTO.BookAnalyticsDTO(t.book.name, COUNT(t)) " +
            "FROM Transaction t " +
            "WHERE t.isIssueOperation = true " +
            "GROUP BY t.book.name ORDER BY COUNT(t) DESC")
    List<BookAnalyticsDTO> findTopBorrowedBooks();
}
