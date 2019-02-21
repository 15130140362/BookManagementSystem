package com.example.projectmanagement.tableEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


public interface userInfoRepository extends JpaRepository<SysUser,Integer> {
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO sys_user_roles(sys_user_id,roles_id) VALUES (?1,?2)",nativeQuery = true)
    void insertToSysRole(Integer integer1,Integer integer2);


    @Transactional
    @Modifying
    @Query(value = "INSERT INTO sys_user  VALUES (?1,?2,?3)",nativeQuery = true)
    void save(Integer id,String username,String password);

    Optional<SysUser> findByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_user_roles where sys_user_id=?1",nativeQuery = true)
    void deleteTheLibrarian(Integer lon);

    @Transactional
    @Modifying
    @Query(value="SELECT sys_user.id,sys_user.username,sys_user.`password`  \n" +
            "FROM sys_user,sys_user_roles where sys_user.id=sys_user_roles.sys_user_id \n" +
            "and sys_user_roles.roles_id=?1 ",nativeQuery = true)
    List<Object[]> findByRoles(Integer temp);

    @Transactional
    @Query(value = "SELECT roles_id FROM sys_user_roles WHERE sys_user_id=?1",nativeQuery = true)
    Integer findRoleId(Integer id);

    @Transactional
    @Query(value = "SELECT roles_id FROM sys_user_roles WHERE sys_user_id=?1",nativeQuery = true)
    Integer findRoleID(Integer userId);

}
