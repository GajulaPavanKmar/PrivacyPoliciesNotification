package com.privacypolicies.PrivacyPoliciesNotification.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity(debug = true)
public class ProjectSecurityConfig {

    // Define the SecurityFilterChain bean
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/","/home", "/assets/css/**", "/assets/js/**", "/assets/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")                      // Custom login page
                        .loginProcessingUrl("/signin")            // URL to submit the username and password
                        .usernameParameter("userName")            // Parameter for username
                        .passwordParameter("userPassword")
                        .defaultSuccessUrl("/dashboard", true)    // Redirect after successful login
                        .failureUrl("/login?error=true")          // Redirect after login failure
                        .permitAll())                             // Allow access to all users to the login page
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED) // Define session creation policy
                        .sessionFixation(sessionFixation -> sessionFixation.migrateSession()) // Use new session upon authentication
                        .invalidSessionUrl("/login?invalid") // Redirect to login on invalid session
                        .maximumSessions(1) // Allow only one session per user
                        .maxSessionsPreventsLogin(true) // Prevent login if max sessions exceeded
                        .expiredUrl("/login?expired") // Redirect to login on session expired
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

}
