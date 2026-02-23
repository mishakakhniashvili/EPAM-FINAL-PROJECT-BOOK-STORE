package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(e -> mapper.map(e, EmployeeDTO.class))
                .toList();
    }

    @Override
    public EmployeeDTO getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + email));
        return mapper.map(employee, EmployeeDTO.class);
    }

    @Override
    @Transactional
    public EmployeeDTO addEmployee(EmployeeDTO dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistException("Employee already exists: " + dto.getEmail());
        }

        Employee employee = mapper.map(dto, Employee.class);
        employee.setPassword(encodeIfNeeded(dto.getPassword()));

        Employee saved = employeeRepository.save(employee);
        return mapper.map(saved, EmployeeDTO.class);
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO dto) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + email));

        employee.setEmail(email);
        employee.setName(dto.getName());
        employee.setPassword(encodeIfNeeded(dto.getPassword()));
        employee.setBirthDate(dto.getBirthDate());
        employee.setPhone(dto.getPhone());

        Employee saved = employeeRepository.save(employee);
        return mapper.map(saved, EmployeeDTO.class);
    }

    @Override
    @Transactional
    public void deleteEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + email));
        employeeRepository.delete(employee);
    }

    private String encodeIfNeeded(String password) {
        if (password == null) return null;
        if (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) {
            return password;
        }
        return passwordEncoder.encode(password);
    }


    @Transactional
    @Override
    public EmployeeDTO patchEmployeeByEmail(String email, EmployeeDTO dto) {
        Employee emp = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee email not found: " + email));

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            emp.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getName() != null && !dto.getName().isBlank()) {
            emp.setName(dto.getName());
        }
        if (dto.getBirthDate() != null) {
            emp.setBirthDate(dto.getBirthDate());
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            emp.setPhone(dto.getPhone());
        }

        return mapper.map(employeeRepository.save(emp), EmployeeDTO.class);
    }


}
