package com.example.projectmanagement.addBook;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface bookTypeRepository extends JpaRepository<bookType, Integer> {
    @Transactional
    @Query(value = "SELECT  book_type.id,book_type.typename\n" +
            "FROM book_type \n" +
            "WHERE book_type.typename LIKE ?1",nativeQuery = true)
    List<bookType> findByTypename(String typeName);
}
