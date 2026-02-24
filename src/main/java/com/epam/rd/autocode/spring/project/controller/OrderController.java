package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final EmployeeRepository employeeRepository;
    private final Validator validator;

    @GetMapping("/by_client/{email:.+}")
    public List<OrderDTO> getOrdersByClient(@PathVariable String email, Authentication auth) {
        boolean isClient = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_CLIENT".equals(a.getAuthority()));

        if (isClient && !auth.getName().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Clients can access only their own orders");
        }

        return orderService.getOrdersByClient(email);
    }

    @GetMapping("/by_employee/{email:.+}")
    public List<OrderDTO> getOrdersByEmployee(@PathVariable String email) {
        return orderService.getOrdersByEmployee(email);
    }

    @GetMapping("/my")
    public List<OrderDTO> myOrders(Authentication auth) {
        boolean isEmployee = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
        boolean isClient = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        if (isEmployee) return orderService.getOrdersByEmployee(auth.getName());
        if (isClient) return orderService.getOrdersByClient(auth.getName());

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @PostMapping
    public OrderDTO addOrder(@RequestBody OrderDTO dto, Authentication auth) {

        boolean isEmployee = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
        boolean isClient = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        if (isEmployee) {
            dto.setEmployeeEmail(auth.getName()); // auto-fill
            // dto.clientEmail must be provided by employee
        } else if (isClient) {
            dto.setClientEmail(auth.getName()); // auto-fill

            String anyEmployeeEmail = employeeRepository.findEmployeeEmailsOrderByLoad().stream()
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No employees available"));
            dto.setEmployeeEmail(anyEmployeeEmail); // auto-fill
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Set<ConstraintViolation<OrderDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }

        return orderService.addOrder(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id, Authentication auth) {

        var order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));

        boolean isEmployee = auth.getAuthorities().stream().anyMatch(a -> "ROLE_EMPLOYEE".equals(a.getAuthority()));
        boolean isClient = auth.getAuthorities().stream().anyMatch(a -> "ROLE_CLIENT".equals(a.getAuthority()));

        if (isClient) {
            String ownerEmail = order.getClient().getEmail();
            if (!auth.getName().equalsIgnoreCase(ownerEmail)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Clients can delete only their own orders");
            }
        } else if (!isEmployee) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        orderRepository.delete(order);
    }
}

