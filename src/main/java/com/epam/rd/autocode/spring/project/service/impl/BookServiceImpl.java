package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final ModelMapper mapper;

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(c -> mapper.map(c, BookDTO.class))
                .toList();
    }

    @Override
    public BookDTO getBookByName(String name) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found: " + name));
        return mapper.map(book, BookDTO.class);

    }

    @Override
    @Transactional
    public BookDTO updateBookByName(String name, BookDTO dto) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found: " + name));
        book.setName(dto.getName());
        book.setGenre(dto.getGenre());
        book.setAuthor(dto.getAuthor());
        book.setDescription(dto.getDescription());
        book.setPublicationDate(dto.getPublicationDate());
        book.setLanguage(dto.getLanguage());
        book.setAgeGroup(dto.getAgeGroup());
        book.setPages(dto.getPages());
        book.setPrice(dto.getPrice());
        book.setCharacteristics(dto.getCharacteristics());

        return mapper.map(bookRepository.save(book), BookDTO.class);
    }

    @Override
    @Transactional
    public void deleteBookByName(String name) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found: " + name));
        bookRepository.delete(book);
    }

    @Override
    @Transactional
    public BookDTO addBook(BookDTO dto) {
        if (bookRepository.existsByName(dto.getName())) {
            throw new AlreadyExistException("Book already exists: " + dto.getName());
        }

        Book entity = mapper.map(dto, Book.class);
        Book saved = bookRepository.save(entity);
        return mapper.map(saved, BookDTO.class);
    }

    @Override
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Transactional
    @Override
    public BookDTO patchBookByName(String name, BookDTO dto) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found: " + name));

        if (dto.getName() != null && !dto.getName().isBlank()) book.setName(dto.getName());
        if (dto.getAuthor() != null && !dto.getAuthor().isBlank()) book.setAuthor(dto.getAuthor());
        if (dto.getGenre() != null && !dto.getGenre().isBlank()) book.setGenre(dto.getGenre());
        if (dto.getPrice() != null) book.setPrice(dto.getPrice());
        if (dto.getPublicationDate() != null) book.setPublicationDate(dto.getPublicationDate());
        if (dto.getPages() != null) book.setPages(dto.getPages());
        if (dto.getLanguage() != null) book.setLanguage(dto.getLanguage());
        if (dto.getAgeGroup() != null) book.setAgeGroup(dto.getAgeGroup());
        if (dto.getCharacteristics() != null && !dto.getCharacteristics().isBlank())
            book.setCharacteristics(dto.getCharacteristics());
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) book.setDescription(dto.getDescription());

        return mapper.map(bookRepository.save(book), BookDTO.class);
    }

}