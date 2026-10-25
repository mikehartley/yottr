package uk.co.yottr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Configuration
@EnableWebSecurity
@Import(PasswordEncoderConfig.class)
public class SecurityConfig {

    // The UserDetailsService ("jpaUserDetailsService") and PasswordEncoder beans are picked up automatically.

    /*
     * Authorities are the bare role names (ADMIN, FREE), as Spring Security 3 expected, so these use
     * hasAuthority: since Spring Security 4, hasRole looks for a ROLE_ prefix.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/s/**").hasAuthority("FREE")
                .requestMatchers("/resources/**").permitAll()
                .requestMatchers("/", "/signup*", "/denied*").permitAll()
//                .anyRequest().authenticated()
                .anyRequest().permitAll())
            .formLogin(form -> form
                .loginPage("/index")
                .permitAll())
            .logout(logout -> logout
                .permitAll())
            .csrf(AbstractHttpConfigurer::disable);
//                .logout().invalidateHttpSession(true).logoutSuccessUrl("/logout").logoutUrl("/logout")
////            .and()
////                .csrf()
//            .and()
//                .sessionManagement().maximumSessions(1).expiredUrl("/login").and().invalidSessionUrl("/login")
//            .and()
//                .exceptionHandling().accessDeniedPage("/denied");

        return http.build();
    }
}
