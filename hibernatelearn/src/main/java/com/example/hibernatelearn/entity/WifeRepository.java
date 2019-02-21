package com.example.hibernatelearn.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WifeRepository extends JpaRepository<Wife,Integer> {
   Wife findByName(String name);
}
