package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }
    @GetMapping("/{name}")
    public BookDTO getBookByName(@PathVariable String name) {
        return bookService.getBookByName(name);
    }

    @PostMapping
    public BookDTO addBook(@RequestBody @Valid BookDTO dto) {
        return bookService.addBook(dto);
    }

    @PutMapping("/{name}")
    public BookDTO updateBook(@PathVariable String name, @RequestBody @Valid BookDTO dto) {
        return bookService.updateBookByName(name, dto);
    }

    @DeleteMapping("/{name}")
    public void deleteBook(@PathVariable String name) {
        bookService.deleteBookByName(name);
    }
}