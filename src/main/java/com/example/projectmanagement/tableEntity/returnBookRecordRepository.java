package com.example.projectmanagement.tableEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface returnBookRecordRepository extends JpaRepository<returnBookRecord,Integer> {
    List<returnBookRecord> findByReaderName(String  userName);
}
