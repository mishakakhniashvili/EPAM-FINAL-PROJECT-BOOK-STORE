package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private ModelMapper mapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private EmployeeServiceImpl service;

    @Test
    void addEmployee_whenExists_throwsAlreadyExist() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("e@mail.com");

        when(employeeRepository.existsByEmail("e@mail.com")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> service.addEmployee(dto));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployeeByEmail_updatesAndEncodes() {
        String email = "e@mail.com";

        Employee existing = new Employee();
        existing.setEmail(email);
        existing.setPassword("old");

        EmployeeDTO dto = new EmployeeDTO();
        dto.setName("New");
        dto.setPassword("plain");
        dto.setBirthDate(LocalDate.of(2000, 1, 1));
        dto.setPhone("+995");

        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("plain")).thenReturn("ENC");
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.map(any(Employee.class), eq(EmployeeDTO.class))).thenReturn(dto);

        service.updateEmployeeByEmail(email, dto);

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        Employee saved = captor.getValue();

        assertEquals(email, saved.getEmail());
        assertEquals("New", saved.getName());
        assertEquals("ENC", saved.getPassword());
        assertEquals(LocalDate.of(2000, 1, 1), saved.getBirthDate());
        assertEquals("+995", saved.getPhone());
    }

    @Test
    void deleteEmployee_missing_throwsNotFound() {
        when(employeeRepository.findByEmail("x@mail.com")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.deleteEmployeeByEmail("x@mail.com"));
    }
}