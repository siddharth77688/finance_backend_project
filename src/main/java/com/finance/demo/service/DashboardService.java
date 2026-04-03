package com.finance.demo.service;

import com.finance.demo.dto.DashboardSummaryDto;
import com.finance.demo.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    // Injecting the repository to fetch financial data
    private final FinancialRecordRepository recordRepository;

    public DashboardSummaryDto getDashboardSummary() {
        // Fetching total records, total income, and total expenses from the repository
        Long totalRecords = recordRepository.countTotalRecords();
        BigDecimal totalIncome = recordRepository.getTotalIncome();
        BigDecimal totalExpense = recordRepository.getTotalExpense();

        // Handling null values to avoid NullPointerException
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        // Calculating net balance
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        // Fetching category-wise summary data
        List<Object[]> categoryData = recordRepository.getCategoryWiseSummary();
        Map<String, BigDecimal> categoryBreakdown = new HashMap<>();
        for (Object[] row : categoryData) {
            String category = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            categoryBreakdown.put(category, amount);
        }

        // Building and returning the dashboard summary DTO
        return DashboardSummaryDto.builder()
                .totalRecords(totalRecords)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .categoryBreakdown(categoryBreakdown)
                .build();
    }
}
