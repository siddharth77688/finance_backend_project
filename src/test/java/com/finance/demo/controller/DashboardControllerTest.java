package com.finance.demo.controller;

import com.finance.demo.dto.DashboardSummaryDto;
import com.finance.demo.security.JwtTokenProvider;
import com.finance.demo.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void getDashboardSummary_success() throws Exception {
        DashboardSummaryDto summary = DashboardSummaryDto.builder()
                .totalRecords(5L)
                .totalIncome(new BigDecimal("50000"))
                .totalExpense(new BigDecimal("17000"))
                .netBalance(new BigDecimal("33000"))
                .categoryBreakdown(Map.of("Sales", new BigDecimal("50000")))
                .build();

        when(dashboardService.getDashboardSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").value(5))
                .andExpect(jsonPath("$.totalIncome").value(50000))
                .andExpect(jsonPath("$.totalExpense").value(17000))
                .andExpect(jsonPath("$.netBalance").value(33000))
                .andExpect(jsonPath("$.categoryBreakdown.Sales").value(50000));
    }
}
