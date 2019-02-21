package com.example.hibernatelearn.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HusbandRepository extends JpaRepository<Husband,Integer> {
  //   Husband save(Husband husband);
     Husband findByName(String name);
}
