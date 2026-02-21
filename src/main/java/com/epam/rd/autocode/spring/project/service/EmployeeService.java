package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeByEmail(String email);

    EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employee);

    void deleteEmployeeByEmail(String email);

    EmployeeDTO addEmployee(EmployeeDTO employee);

    EmployeeDTO patchEmployeeByEmail(String email, EmployeeDTO dto);


}
