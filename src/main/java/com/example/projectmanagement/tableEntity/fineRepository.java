package com.example.projectmanagement.tableEntity;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface fineRepository extends JpaRepository<fine, Integer> {
    @Transactional
    @Query(value = "SELECT fine_value FROM fine WHERE fine.return_date LIKE ?1", nativeQuery = true)
    List<Object> getFineValue(String date);
    List<fine> findByUsername(String username);
    Optional<fine> findByBookIdAndUsernameAndBorrowDate(Integer bookId,String username,String borrowDate);
}
