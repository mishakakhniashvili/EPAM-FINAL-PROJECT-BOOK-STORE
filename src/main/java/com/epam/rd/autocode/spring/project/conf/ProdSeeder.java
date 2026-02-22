package com.epam.rd.autocode.spring.project.conf;

import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class ProdSeeder implements ApplicationRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String email = System.getenv().getOrDefault("DEMO_EMPLOYEE_EMAIL", "admin@example.com");
        String pass  = System.getenv().getOrDefault("DEMO_EMPLOYEE_PASSWORD", "admin123");

        if (!employeeRepository.existsByEmail(email)) {
            Employee e = new Employee();
            e.setEmail(email);
            e.setName("Admin");
            e.setBirthDate(LocalDate.of(1990, 1, 1));
            e.setPhone("000");
            e.setPassword(passwordEncoder.encode(pass));
            employeeRepository.save(e);
        }
    }
}