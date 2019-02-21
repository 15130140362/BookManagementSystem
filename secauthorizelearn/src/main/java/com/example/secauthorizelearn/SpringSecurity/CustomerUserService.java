package com.example.secauthorizelearn.SpringSecurity;
import com.example.secauthorizelearn.tableEntity.SysUser;
import com.example.secauthorizelearn.tableEntity.userInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomerUserService implements UserDetailsService {

    @Autowired
    userInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userInfoRepository.findByUsername(username);
        System.out.println(user.toString());
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        return user;
    }
}
