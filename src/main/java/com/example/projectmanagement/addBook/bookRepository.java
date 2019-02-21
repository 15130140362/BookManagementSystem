package com.example.projectmanagement.addBook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface bookRepository extends JpaRepository<book,Integer> {
      List<book> findByName(String str);
      @Transactional
      @Modifying
      @Query(value ="UPDATE book SET sur_plus=sur_plus+1 WHERE id=?1",nativeQuery = true)
      void  updateSurPlus(Integer bookId);


      @Transactional
      @Query(value = "SELECT  one_book.id,book.`name`,borrow_book_record.borrowdate,borrow_book_record.should_return_date,book.book_pic_number,book.publisher " +
              "FROM  one_book,borrow_book_record,book WHERE  " +
              "borrow_book_record.book_id=one_book.id AND book.id=one_book.book_id " +
              "AND one_book.book_state=?1 AND borrow_book_record.reader_name=?2",nativeQuery = true)
      List<Object[]> findReaderLendAndOverDueBook(String state,String username);

      @Transactional
      @Query(value = "SELECT * FROM book WHERE `name` LIKE ?1",nativeQuery = true)
      List<Object[]> getSearchBookInformation(String bookName);

      @Transactional
      @Modifying
      @Query(value = "UPDATE book SET book.sur_plus=?1 WHERE book.id=?2",nativeQuery = true)
      void borrowBookMinusSurPlus(Integer surPlus, Integer bookId);

}
