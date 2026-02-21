package com.epam.rd.autocode.spring.project.security;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Employee emp = employeeRepository.findByEmail(email).orElse(null);
        if (emp != null) {
            return User.withUsername(emp.getEmail())
                    .password(emp.getPassword())
                    .roles("EMPLOYEE")
                    .build();
        }

        Client client = clientRepository.findByEmail(email).orElse(null);
        if (client != null) {
            return User.withUsername(client.getEmail())
                    .password(client.getPassword())
                    .roles("CLIENT")
                    .disabled(client.isBlocked())
                    .build();
        }

        throw new UsernameNotFoundException("User not found: " + email);
    }
}

