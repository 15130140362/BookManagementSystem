package com.example.projectmanagement.tableEntity;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface reserveBookRepository extends JpaRepository<reserveBook,Integer> {
    List<reserveBook> findByUsername(String username);
    List<reserveBook> findByUsernameAndIsVaild(String username,String isValid);
    Optional<reserveBook> findByBookIdAndIsVaild(Integer bokoId,String isvalid);
}
