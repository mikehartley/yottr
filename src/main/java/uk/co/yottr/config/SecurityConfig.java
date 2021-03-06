package uk.co.yottr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Configuration
@EnableWebMvcSecurity
@Import(PasswordEncoderConfig.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("jpaUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/admin/**").access("hasRole('ADMIN')")
                .antMatchers("/s/**").access("hasRole('FREE')")
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/", "/signup*", "/denied*").permitAll()
//                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/index")
                .permitAll()
                .and()
            .logout()
                .permitAll()
                .and()
            .csrf().disable();
//                .logout().invalidateHttpSession(true).logoutSuccessUrl("/logout").logoutUrl("/logout")
////            .and()
////                .csrf()
//            .and()
//                .sessionManagement().maximumSessions(1).expiredUrl("/login").and().invalidSessionUrl("/login")
//            .and()
//                .exceptionHandling().accessDeniedPage("/denied");
    }
}
