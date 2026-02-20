package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    @NotBlank @Email
    private String clientEmail;
    @NotBlank @Email
    private String employeeEmail;
    private LocalDateTime orderDate;
    private BigDecimal price;
    @NotEmpty @Valid
    private List<BookItemDTO> bookItems;
}
