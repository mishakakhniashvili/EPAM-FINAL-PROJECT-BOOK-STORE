package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // who bought
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID")
    private Client client;
    // who processed
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID")
    private Employee employee;
    @Column(name = "ORDER_DATE", nullable = false)
    private LocalDateTime orderDate;
    @Column(name = "PRICE", precision = 19, scale = 2)
    private BigDecimal price;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookItem> bookItems = new ArrayList<>();

    public Order() {
    }

    public Order(Long id, Client client, Employee employee, LocalDateTime orderDate, BigDecimal price, List<BookItem> bookItems) {
        this.id = id;
        this.client = client;
        this.employee = employee;
        this.orderDate = orderDate;
        this.price = price;
        this.bookItems = bookItems;
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<BookItem> getBookItems() {
        return bookItems;
    }

    public void setBookItems(List<BookItem> bookItems) {
        this.bookItems = bookItems;
    }
}
