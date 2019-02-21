package com.example.projectmanagement.tableEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface borrowBookRecordRepository extends JpaRepository<borrowBookRecord,Integer> {
    List<borrowBookRecord> findByReaderName(String name);
    Optional<borrowBookRecord> findByBookId(Integer id);
}
