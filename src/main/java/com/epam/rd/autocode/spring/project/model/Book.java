package com.epam.rd.autocode.spring.project.model;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "BOOKS")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;
    @Column(name = "GENRE")
    private String genre;
    @Enumerated(EnumType.STRING)
    @Column(name = "AGE_GROUP")
    private AgeGroup ageGroup;
    @Column(name = "PRICE", precision = 19, scale = 2)
    private BigDecimal price;
    @Column(name = "PUBLICATION_DATE")
    private LocalDate publicationDate;
    @Column(name = "AUTHOR")
    private String author;
    @Column(name = "NUMBER_OF_PAGES")
    private Integer pages;
    @Column(name = "CHARACTERISTICS", length = 2000)
    private String characteristics;
    @Column(name = "DESCRIPTION", length = 4000)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "LANGUAGE")
    private Language language;

    public Book() {
    }

    public Book(Long id,
                String name,
                String genre,
                AgeGroup ageGroup,
                BigDecimal price,
                LocalDate publicationDate,
                String author,
                Integer pages,
                String characteristics,
                String description,
                Language language) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.ageGroup = ageGroup;
        this.price = price;
        this.publicationDate = publicationDate;
        this.author = author;
        this.pages = pages;
        this.characteristics = characteristics;
        this.description = description;
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
