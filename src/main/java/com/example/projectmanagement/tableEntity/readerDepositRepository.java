package com.example.projectmanagement.tableEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface readerDepositRepository extends JpaRepository<readerDeposit,Integer> {
    @Transactional
    @Query(value = "SELECT * FROM reader_deposit WHERE reader_deposit.date LIKE ?1",nativeQuery = true)
    List<Object[]> getDeposit(String dateTime);
}
