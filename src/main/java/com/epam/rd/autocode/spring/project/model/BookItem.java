package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "BOOK_ITEMS")
public class BookItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // part of which order
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;
    // which book
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_ID")
    private Book book;
    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    public BookItem() {
    }

    public BookItem(Long id, Order order, Book book, Integer quantity) {
        this.id = id;
        this.order = order;
        this.book = book;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
