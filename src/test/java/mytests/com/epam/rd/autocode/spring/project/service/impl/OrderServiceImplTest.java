package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private BookRepository bookRepository;
    @Mock private ModelMapper mapper;

    @InjectMocks private OrderServiceImpl service;

    @Test
    void addOrder_emptyItems_throwsIllegalArgument() {
        OrderDTO dto = new OrderDTO();
        dto.setBookItems(List.of());

        assertThrows(IllegalArgumentException.class, () -> service.addOrder(dto));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void addOrder_calculatesTotalPrice() {
        // dto
        OrderDTO dto = new OrderDTO();
        dto.setClientEmail("c@mail.com");
        dto.setEmployeeEmail("e@mail.com");

        BookItemDTO i1 = new BookItemDTO();
        i1.setBookName("B1");
        i1.setQuantity(2);

        BookItemDTO i2 = new BookItemDTO();
        i2.setBookName("B2");
        i2.setQuantity(3);

        dto.setBookItems(List.of(i1, i2));

        // repos
        when(clientRepository.findByEmail("c@mail.com")).thenReturn(Optional.of(new Client()));
        when(employeeRepository.findByEmail("e@mail.com")).thenReturn(Optional.of(new Employee()));

        Book b1 = new Book();
        b1.setName("B1");
        b1.setPrice(new BigDecimal("10.00")); // 2 * 10 = 20

        Book b2 = new Book();
        b2.setName("B2");
        b2.setPrice(new BigDecimal("5.00"));  // 3 * 5 = 15

        when(bookRepository.findByName("B1")).thenReturn(Optional.of(b1));
        when(bookRepository.findByName("B2")).thenReturn(Optional.of(b2));

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        service.addOrder(dto);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        Order saved = captor.getValue();
        assertNotNull(saved.getOrderDate());
        assertEquals(new BigDecimal("35.00"), saved.getPrice()); // 20 + 15
    }

    @Test
    void addOrder_missingClient_throwsNotFound() {
        OrderDTO dto = new OrderDTO();
        dto.setClientEmail("missing@mail.com");
        dto.setEmployeeEmail("e@mail.com");

        BookItemDTO i1 = new BookItemDTO();
        i1.setBookName("B1");
        i1.setQuantity(1);
        dto.setBookItems(List.of(i1));

        when(clientRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addOrder(dto));
        verifyNoInteractions(orderRepository);
    }
}