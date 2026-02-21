package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ClientService clientService;
    private final EmployeeService employeeService;

    @GetMapping
    public Map<String, Object> getProfile(Authentication auth) {
        String email = auth.getName();
        boolean isEmployee = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_EMPLOYEE"::equals);

        if (isEmployee) {
            EmployeeDTO dto = employeeService.getEmployeeByEmail(email);
            return Map.of("role", "EMPLOYEE", "profile", dto);
        } else {
            ClientDTO dto = clientService.getClientByEmail(email);
            return Map.of("role", "CLIENT", "profile", dto);
        }
    }

    @PutMapping("/employee")
    public Map<String, Object> updateMyEmployeeProfile(Authentication auth, @RequestBody @Valid EmployeeDTO dto) {
        String email = auth.getName();
        EmployeeDTO updated = employeeService.updateEmployeeByEmail(email, dto);
        return Map.of("role", "EMPLOYEE", "profile", updated);
    }
    @PutMapping
    public Map<String, Object> updateMyClientProfile(Authentication auth, @RequestBody @Valid ClientDTO dto) {
        String email = auth.getName();
        ClientDTO updated = clientService.updateClientByEmail(email, dto);
        return Map.of("role", "CLIENT", "profile", updated);
    }

    @DeleteMapping
    public void deleteMyAccount(Authentication auth) {
        clientService.deleteClientByEmail(auth.getName());
    }
}