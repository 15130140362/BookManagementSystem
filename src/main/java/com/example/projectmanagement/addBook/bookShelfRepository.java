package com.example.projectmanagement.addBook;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface bookShelfRepository extends JpaRepository<bookShelf,Integer> {
    @Transactional
    @Query(value ="SELECT id FROM  book_shelf WHERE type=-1;",nativeQuery = true)
    List<Integer> findValidBookShelf();

    @Transactional
    @Modifying
    @Query(value = "UPDATE book_shelf SET book_shelf.type=?1 WHERE id>=?2 and id<=?3",nativeQuery = true)
    void updateBookType(Integer bookType, Integer Max,Integer Min);

    List<bookShelf> findByType(Integer typeId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE book_shelf SET book_shelf.type=?2 WHERE book_shelf.type=?1",nativeQuery = true)
    void updateBookType(Integer bookType,Integer value);
}
