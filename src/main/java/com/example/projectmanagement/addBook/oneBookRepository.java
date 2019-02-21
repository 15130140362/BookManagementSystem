package com.example.projectmanagement.addBook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface oneBookRepository extends JpaRepository<oneBook,Integer> {
    List<oneBook> findByBookId(Integer BookID);
    List<oneBook> findByBookIdAndBookState(Integer BookID,String bookState);
    @Transactional
    @Modifying
    @Query(value = "update one_book set book_state=?3,trade_id=?5 where bookid=?1 AND book_state=?4 ORDER BY id LIMIT ?2",nativeQuery = true)
    public void updateBookState(Integer bookId,Integer num,String newbookState,String oldbookState,Integer tradeId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE one_book SET book_state=?2 WHERE id= ?1",nativeQuery = true)
    void updateReserveState(Integer tradeId,String state);

    @Transactional
    @Modifying
    @Query(value = "UPDATE one_book SET trade_id=?2 WHERE trade_id= ?1",nativeQuery = true)
    void updateTradeId(Integer tradeId,Integer newTrade);


    @Transactional
    @Modifying
    @Query(value = "UPDATE one_book SET book_state=?2 WHERE id=?1",nativeQuery = true)
    void updateBookStateBybookId(Integer id,String bookState);
}
