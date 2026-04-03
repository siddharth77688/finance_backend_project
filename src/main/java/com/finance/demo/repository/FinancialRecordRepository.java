package com.finance.demo.repository;

import com.finance.demo.entity.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// The FinancialRecordRepository interface extends JpaRepository, providing CRUD operations for the FinancialRecord entity.
// Custom query methods are defined to retrieve financial records based on various criteria such as user ID, record type, category, and transaction date range. 
// Additionally, custom queries are implemented to calculate total income, total expenses, count total records, and get category-wise summaries using JPQL .
@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // Custom query method to find financial records created by a specific user, identified by their user ID.
    List<FinancialRecord> findByCreatedById(Long userId);

    // Custom query method to find financial records of a specific type.
    List<FinancialRecord> findByType(FinancialRecord.RecordType type);

    // Custom query method to find financial records in a specific category.
    List<FinancialRecord> findByCategory(String category);

    // Custom query method to find financial records within a specific date range.
    List<FinancialRecord> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(fr.amount) FROM FinancialRecord fr WHERE fr.type = 'INCOME'")
    BigDecimal getTotalIncome();

    @Query("SELECT SUM(fr.amount) FROM FinancialRecord fr WHERE fr.type = 'EXPENSE'")
    BigDecimal getTotalExpense();

    @Query("SELECT COUNT(fr) FROM FinancialRecord fr")
    Long countTotalRecords();

    @Query("SELECT fr.category, SUM(fr.amount) FROM FinancialRecord fr GROUP BY fr.category")
    List<Object[]> getCategoryWiseSummary();
}
