package com.example.projectmanagement.tableEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface announcementMessageRepository extends JpaRepository<announcementMessage, Integer> {
    List<announcementMessage> findByName(String name);
}
