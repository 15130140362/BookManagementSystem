package com.example.projectmanagement.SpringSecurity;


import com.example.projectmanagement.tableEntity.SysUser;
import com.example.projectmanagement.tableEntity.userInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomerUserService implements UserDetailsService {

    @Autowired
    userInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SysUser> optionalSysUser = userInfoRepository.findByUsername(username);
        if (!optionalSysUser.isPresent()) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        return optionalSysUser.get();
    }
}
