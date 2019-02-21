package com.example.projectmanagement.tableEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface sysInfoSettingRepository extends JpaRepository<SysInfoSetting,String> {
    @Transactional
    @Modifying
    @Query(value = "UPDATE sys_info_setting SET info_value=?2 WHERE info_name=?1",nativeQuery = true)
    void  updateValue(String id,String value);
}
