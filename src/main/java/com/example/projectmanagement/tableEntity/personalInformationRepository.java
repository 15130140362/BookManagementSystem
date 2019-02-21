package com.example.projectmanagement.tableEntity;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface personalInformationRepository extends JpaRepository<personalInformation, Integer> {
    Optional<personalInformation> findByUserid(Integer userId);
}
