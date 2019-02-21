package com.example.secauthorizelearn.tableEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface userInfoRepository extends JpaRepository<SysUser,Long> {
    SysUser findByUsername(String username);
}
