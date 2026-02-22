package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock private BookRepository bookRepository;
    @Mock private ModelMapper mapper;

    @InjectMocks private BookServiceImpl service;

    @Test
    void addBook_whenExists_throwsAlreadyExist() {
        BookDTO dto = new BookDTO();
        dto.setName("B");

        when(bookRepository.existsByName("B")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> service.addBook(dto));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void getBookByName_missing_throwsNotFound() {
        when(bookRepository.findByName("X")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getBookByName("X"));
    }

    @Test
    void updateBookByName_updatesFieldsAndSaves() {
        String name = "Old";

        Book existing = new Book();
        existing.setName(name);

        BookDTO dto = new BookDTO();
        dto.setName("New");
        dto.setAuthor("A");
        dto.setGenre("G");
        dto.setDescription("D");
        dto.setCharacteristics("C");
        dto.setPages(100);
        dto.setPrice(new BigDecimal("9.99"));

        when(bookRepository.findByName(name)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.map(any(Book.class), eq(BookDTO.class))).thenReturn(dto);

        service.updateBookByName(name, dto);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        Book saved = captor.getValue();

        assertEquals("New", saved.getName());
        assertEquals("A", saved.getAuthor());
        assertEquals("G", saved.getGenre());
        assertEquals("D", saved.getDescription());
        assertEquals("C", saved.getCharacteristics());
        assertEquals(100, saved.getPages());
        assertEquals(new BigDecimal("9.99"), saved.getPrice());
    }
}