package com.example.projectmanagement.tableEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface deleteBookRecordRepository extends JpaRepository<deleteBookRecord,Integer> {
    Optional<deleteBookRecord> findByBookId(Integer bookId);
    @Transactional
    @Query(value = "SELECT * FROM delete_book_record\n" +
            "WHERE delete_book_record.book_name LIKE ?1",nativeQuery = true)
    List<deleteBookRecord> findByBookName(String bookName);
}
