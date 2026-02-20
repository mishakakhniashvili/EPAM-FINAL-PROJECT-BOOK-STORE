package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{email:.+}")
    public EmployeeDTO getEmployeeByEmail(@PathVariable String email) {
        return employeeService.getEmployeeByEmail(email);
    }
    @Valid
    @PostMapping
    public EmployeeDTO addEmployee(@RequestBody @Valid EmployeeDTO dto) {
        return employeeService.addEmployee(dto);
    }
    @Valid
    @PutMapping("/{email:.+}")
    public EmployeeDTO updateEmployeeByEmail(@PathVariable String email, @RequestBody @Valid EmployeeDTO dto) {
        return employeeService.updateEmployeeByEmail(email, dto);
    }

    @DeleteMapping("/{email:.+}")
    public void deleteEmployeeByEmail(@PathVariable String email) {
        employeeService.deleteEmployeeByEmail(email);
    }
}
