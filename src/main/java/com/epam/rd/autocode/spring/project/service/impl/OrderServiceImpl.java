package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;
    private final ModelMapper mapper;

    @Override
    public List<OrderDTO> getOrdersByClient(String email) {
        return orderRepository.findByClient_Email(email)
                .stream()
                .map(o -> mapper.map(o, OrderDTO.class))
                .toList();
    }

    @Override
    public List<OrderDTO> getOrdersByEmployee(String employeeEmail) {
        return orderRepository.findByEmployee_Email(employeeEmail)
                .stream()
                .map(o -> mapper.map(o, OrderDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public OrderDTO addOrder(OrderDTO dto) {
        if (dto.getBookItems() == null || dto.getBookItems().isEmpty()) {
            throw new IllegalArgumentException("bookItems must not be empty");
        }
        Client client = clientRepository.findByEmail(dto.getClientEmail())
                .orElseThrow(() -> new NotFoundException("Client not found: " + dto.getClientEmail()));

        Employee employee = employeeRepository.findByEmail(dto.getEmployeeEmail())
                .orElseThrow(() -> new NotFoundException("Employee not found: " + dto.getEmployeeEmail()));

        Order order = new Order();
        order.setClient(client);
        order.setEmployee(employee);
        order.setOrderDate(dto.getOrderDate() != null ? dto.getOrderDate() : LocalDateTime.now());

        List<BookItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (BookItemDTO itemDTO : dto.getBookItems()) {
            Book book = bookRepository.findByName(itemDTO.getBookName())
                    .orElseThrow(() -> new NotFoundException("Book not found: " + itemDTO.getBookName()));

            Integer qty = itemDTO.getQuantity();

            BookItem item = new BookItem();
            item.setOrder(order);
            item.setBook(book);
            item.setQuantity(qty);

            items.add(item);

            if (book.getPrice() != null) {
                total = total.add(book.getPrice().multiply(BigDecimal.valueOf(qty)));
            }
        }

        order.setBookItems(items);
        order.setPrice(total);

        Order saved = orderRepository.save(order);
        return mapper.map(saved, OrderDTO.class);
    }
}
