package com.finance.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// use @Data for getters, setters, toString, equals, and hashCode methods.
// The @Entity annotation marks this class as a JPA entity, and @Table specifies the name of the database table to be used for mapping. 
// Each field is annotated with @Column to specify column properties, and relationships are defined using @ManyToOne and @JoinColumn. 
// The createdAt and updatedAt fields are automatically managed by Hibernate using @CreationTimestamp and @UpdateTimestamp annotations.
@Entity
@Table(name = "financial_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecord {

    // @GeneratedValue with strategy = GenerationType.IDENTITY indicates that the database will handle the generation of this value, typically using an auto-incrementing column.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    // Enum type to represent the type of financial record (INCOME, EXPENSE, TRANSFER, INVESTMENT).
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecordType type;

    //big decimal field to store the amount of the financial record, with precision and scale defined for accurate financial calculations.
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false)
    private String category;

    private String reference;

    // The fetch type is set to LAZY to optimize performance by loading the related User entity only when it is accessed.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Timestamps for when the record was created and last updated, automatically managed by Hibernate.
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // The @UpdateTimestamp annotation automatically updates the updatedAt field with the current timestamp whenever the entity is updated.
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum RecordType {
        INCOME,
        EXPENSE,
        TRANSFER,
        INVESTMENT
    }
}
