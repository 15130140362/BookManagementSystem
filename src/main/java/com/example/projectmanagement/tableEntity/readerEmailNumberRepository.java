package com.example.projectmanagement.tableEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface readerEmailNumberRepository extends JpaRepository<readerEmailNumber, Integer> {
    Optional<readerEmailNumber> findByEmail(String email);
    Optional<readerEmailNumber> findByUserid(Integer userId);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM reader_email_number WHERE reader_email_number.userid=?1",nativeQuery = true)
    void deleteByUserID(Integer id);
}
