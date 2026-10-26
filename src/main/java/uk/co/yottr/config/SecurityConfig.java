package uk.co.yottr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

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
                .requestMatchers("/resources/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/index", "/signup", "/about", "/error", "/error/**").permitAll()
                // for Prometheus; in production these belong on a management port that isn't public
                .requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()
                .requestMatchers("/actuator/loggers", "/actuator/loggers/**").hasAuthority("ADMIN")
                .requestMatchers("/actuator/**").denyAll()
                // anything not listed above needs a signed-in member, rather than being open by default
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/index")
                .permitAll())
            // CSRF protection is on (the Spring Security default), so logging out and every other change is a POST
            .logout(logout -> logout
                .permitAll());

        return http.build();
    }
}
