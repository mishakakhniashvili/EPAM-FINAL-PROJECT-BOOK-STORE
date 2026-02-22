package com.epam.rd.autocode.spring.project.security;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private ClientRepository clientRepository;

    @InjectMocks private CustomUserDetailsService service;

    @Test
    void loadUser_employeeFound_returnsRoleEmployee() {
        Employee emp = new Employee();
        emp.setEmail("e@mail.com");
        emp.setPassword("p");

        when(employeeRepository.findByEmail("e@mail.com")).thenReturn(Optional.of(emp));

        UserDetails ud = service.loadUserByUsername("e@mail.com");

        assertEquals("e@mail.com", ud.getUsername());
        assertTrue(ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE")));
        verify(clientRepository, never()).findByEmail(anyString());
    }

    @Test
    void loadUser_clientFound_returnsRoleClient() {
        when(employeeRepository.findByEmail("c@mail.com")).thenReturn(Optional.empty());

        Client c = new Client();
        c.setEmail("c@mail.com");
        c.setPassword("p");
        when(clientRepository.findByEmail("c@mail.com")).thenReturn(Optional.of(c));

        UserDetails ud = service.loadUserByUsername("c@mail.com");

        assertEquals("c@mail.com", ud.getUsername());
        assertTrue(ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT")));
    }

    @Test
    void loadUser_missing_throwsUsernameNotFound() {
        when(employeeRepository.findByEmail("x@mail.com")).thenReturn(Optional.empty());
        when(clientRepository.findByEmail("x@mail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("x@mail.com"));
    }
}