package com.finance.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

// financial record data transfer object (DTO) used for 
// transferring financial record data between layers of the application 
// it contains fields for the financial record's 
// id, title, description, type, amount, transaction date, category, reference, created by and created at timestamp 
// it also includes validation annotations to ensure that required fields are not null or blank.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecordDto {
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Type is required")
    private String type;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private String reference;
    private String createdBy;
    private String createdAt;
}
