package com.example.projectmanagement.SpringSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;


@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)//启用springsecurity可以使用注解
public class SpringSecurityConf extends WebSecurityConfigurerAdapter {

    @Bean
    UserDetailsService customUserService() {
        return new CustomerUserService();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService()).passwordEncoder(new MyEncode());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/bookpicture/*").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/homepage/*").permitAll()
                .antMatchers("/global/images/*").permitAll()
                .antMatchers("/talk").permitAll()
                .antMatchers("/getMyPassword").permitAll()
                .antMatchers("/reader/**").hasRole("READER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/librarian/**").hasRole("LIBRARIAN")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login") //将login?error改成error就对了
                .failureUrl("/error").permitAll()
                .and()
                .logout().permitAll()
                .and()
                .headers().frameOptions().sameOrigin();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/global/**");
    }

}
