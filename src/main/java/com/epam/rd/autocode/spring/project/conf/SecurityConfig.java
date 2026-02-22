package com.epam.rd.autocode.spring.project.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ---------- CSRF ----------
                // Use cookie-based CSRF token (needed for fetch() + forms)
                // Ignore H2 console to make it usable during development
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                )

                // ---------- H2 Console ----------
                // Allow H2 console to render in frames (same origin)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // ---------- Authorization rules ----------
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // Public static pages + login page
                        .requestMatchers("/", "/index.html", "/app.js", "/styles.css", "/login", "/login.html").permitAll()

                        // H2 console endpoints
                        .requestMatchers("/h2-console/**").permitAll()

                        // Endpoint to set CSRF cookie (your JS calls it on init)
                        .requestMatchers("/csrf").permitAll()

                        // ---------- BOOKS ----------
                        // Anyone can view books
                        .requestMatchers(HttpMethod.GET, "/books/**").permitAll()
                        // Only employees can create/update/delete books (CRUD write operations)
                        .requestMatchers(HttpMethod.POST, "/books/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT, "/books/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.PATCH, "/books/**").hasRole("EMPLOYEE")
                        // ---------- CLIENT REGISTRATION ----------
                        // Allow creating a new client account
                        .requestMatchers(HttpMethod.POST, "/clients").permitAll()

                        // ---------- ORDERS ----------
                        // Client orders view (legacy endpoint) + employee view by client
                        .requestMatchers(HttpMethod.GET, "/orders/by_client/**").hasAnyRole("CLIENT", "EMPLOYEE")
                        // Current user orders (used by JS)
                        .requestMatchers(HttpMethod.GET, "/orders/my").authenticated()

                        // Create orders (both roles allowed per your setup)
                        .requestMatchers(HttpMethod.POST, "/orders").hasAnyRole("EMPLOYEE", "CLIENT")
                        // Employee: view orders assigned to employee
                        .requestMatchers(HttpMethod.GET, "/orders/by_employee/**").hasRole("EMPLOYEE")
                        // Employee: delete orders
                        .requestMatchers(HttpMethod.DELETE, "/orders/**").hasAnyRole("EMPLOYEE","CLIENT")

                        // ---------- "Who am I" ----------
                        .requestMatchers(HttpMethod.GET, "/me").authenticated()
                        // ---------- PROFILE (self-service) ----------
                        // Anyone logged in can view their own profile (client or employee)
                        .requestMatchers(HttpMethod.GET, "/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/profile").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/profile/employee").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.DELETE, "/profile").hasRole("CLIENT")
                        // Only clients can update/delete their own account (matches project requirements)
                        .requestMatchers(HttpMethod.PUT, "/profile").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.DELETE, "/profile").hasRole("CLIENT")


                        // ---------- EMPLOYEES MANAGEMENT ----------
                        .requestMatchers("/employees/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.PATCH, "/employees/**").hasRole("EMPLOYEE")

                        // ---------- CLIENTS MANAGEMENT (admin/employee-only) ----------
                        .requestMatchers(HttpMethod.GET, "/clients/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/clients").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT, "/clients/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.DELETE, "/clients/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.PATCH, "/clients/**").hasRole("EMPLOYEE")
                        // Everything else requires login
                        .anyRequest().authenticated()
                )

                // Return 401 instead of redirecting for unauthorized API calls
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                // ---------- Form login ----------
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .failureUrl("/login.html?error")
                        .defaultSuccessUrl("/index.html", true)
                        .permitAll()
                )

                // ---------- Logout ----------
                .logout(logout -> logout.logoutSuccessUrl("/index.html"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}