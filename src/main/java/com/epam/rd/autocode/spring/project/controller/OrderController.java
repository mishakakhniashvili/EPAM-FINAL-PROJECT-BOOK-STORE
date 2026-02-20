package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/by_client/{email:.+}")
    public List<OrderDTO> getOrdersByClient(@PathVariable String email) {
        return orderService.getOrdersByClient(email);
    }

    @GetMapping("/by_employee/{email:.+}")
    public List<OrderDTO> getOrdersByEmployee(@PathVariable String email) {
        return orderService.getOrdersByEmployee(email);
    }
    @Valid
    @PostMapping
    public OrderDTO addOrder(@RequestBody @Valid OrderDTO dto) {
        return orderService.addOrder(dto);
    }
}

