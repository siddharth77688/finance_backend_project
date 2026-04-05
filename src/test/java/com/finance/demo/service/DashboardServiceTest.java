package com.finance.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finance.demo.dto.DashboardSummaryDto;
import com.finance.demo.repository.FinancialRecordRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private FinancialRecordRepository recordRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboardSummary_withData() {
        when(recordRepository.countTotalRecords()).thenReturn(5L);
        when(recordRepository.getTotalIncome()).thenReturn(new BigDecimal("50000"));
        when(recordRepository.getTotalExpense()).thenReturn(new BigDecimal("17000"));
        List<Object[]> categoryData = List.of(
                new Object[]{"Sales", new BigDecimal("50000")},
                new Object[]{"IT", new BigDecimal("12000")}
        );
        when(recordRepository.getCategoryWiseSummary()).thenReturn(categoryData);

        DashboardSummaryDto result = dashboardService.getDashboardSummary();

        assertEquals(5L, result.getTotalRecords());
        assertEquals(new BigDecimal("50000"), result.getTotalIncome());
        assertEquals(new BigDecimal("17000"), result.getTotalExpense());
        assertEquals(new BigDecimal("33000"), result.getNetBalance());
        assertEquals(2, result.getCategoryBreakdown().size());
        assertEquals(new BigDecimal("50000"), result.getCategoryBreakdown().get("Sales"));
        assertEquals(new BigDecimal("12000"), result.getCategoryBreakdown().get("IT"));
    }

    @Test
    void getDashboardSummary_withNullIncomeAndExpense() {
        when(recordRepository.countTotalRecords()).thenReturn(0L);
        when(recordRepository.getTotalIncome()).thenReturn(null);
        when(recordRepository.getTotalExpense()).thenReturn(null);
        when(recordRepository.getCategoryWiseSummary()).thenReturn(Collections.emptyList());

        DashboardSummaryDto result = dashboardService.getDashboardSummary();

        assertEquals(0L, result.getTotalRecords());
        assertEquals(BigDecimal.ZERO, result.getTotalIncome());
        assertEquals(BigDecimal.ZERO, result.getTotalExpense());
        assertEquals(BigDecimal.ZERO, result.getNetBalance());
        assertTrue(result.getCategoryBreakdown().isEmpty());
    }

    @Test
    void getDashboardSummary_withNullIncomeOnly() {
        when(recordRepository.countTotalRecords()).thenReturn(1L);
        when(recordRepository.getTotalIncome()).thenReturn(null);
        when(recordRepository.getTotalExpense()).thenReturn(new BigDecimal("5000"));
        when(recordRepository.getCategoryWiseSummary()).thenReturn(Collections.emptyList());

        DashboardSummaryDto result = dashboardService.getDashboardSummary();

        assertEquals(BigDecimal.ZERO, result.getTotalIncome());
        assertEquals(new BigDecimal("-5000"), result.getNetBalance());
    }

    @Test
    void getDashboardSummary_withNullExpenseOnly() {
        when(recordRepository.countTotalRecords()).thenReturn(1L);
        when(recordRepository.getTotalIncome()).thenReturn(new BigDecimal("10000"));
        when(recordRepository.getTotalExpense()).thenReturn(null);
        when(recordRepository.getCategoryWiseSummary()).thenReturn(Collections.emptyList());

        DashboardSummaryDto result = dashboardService.getDashboardSummary();

        assertEquals(BigDecimal.ZERO, result.getTotalExpense());
        assertEquals(new BigDecimal("10000"), result.getNetBalance());
    }
}
