package com.finance.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.demo.dto.FinancialRecordDto;
import com.finance.demo.security.JwtTokenProvider;
import com.finance.demo.service.FinancialRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinancialRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class FinancialRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FinancialRecordService recordService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private FinancialRecordDto testDto;

    @BeforeEach
    void setUp() {
        testDto = FinancialRecordDto.builder()
                .id(1L)
                .title("Q1 Sales")
                .description("Sales revenue")
                .type("INCOME")
                .amount(new BigDecimal("50000.00"))
                .transactionDate(LocalDate.of(2024, 1, 15))
                .category("Sales")
                .reference("REF-001")
                .createdBy("admin")
                .createdAt("2024-01-15T10:00")
                .build();
    }

    @Test
    void createRecord_success() throws Exception {
        when(recordService.createRecord(any(FinancialRecordDto.class))).thenReturn(testDto);

        mockMvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Q1 Sales"))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    void getAllRecords_success() throws Exception {
        when(recordService.getAllRecords()).thenReturn(List.of(testDto));

        mockMvc.perform(get("/api/records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Q1 Sales"));
    }

    @Test
    void getRecordById_success() throws Exception {
        when(recordService.getRecordById(1L)).thenReturn(testDto);

        mockMvc.perform(get("/api/records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Q1 Sales"));
    }

    @Test
    void updateRecord_success() throws Exception {
        FinancialRecordDto updatedDto = FinancialRecordDto.builder()
                .id(1L).title("Updated").description("Updated desc")
                .type("EXPENSE").amount(new BigDecimal("30000"))
                .transactionDate(LocalDate.of(2024, 2, 1))
                .category("Operations").reference("REF-UPD")
                .createdBy("admin").createdAt("2024-01-15T10:00").build();

        when(recordService.updateRecord(eq(1L), any(FinancialRecordDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/records/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    void deleteRecord_success() throws Exception {
        doNothing().when(recordService).deleteRecord(1L);

        mockMvc.perform(delete("/api/records/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getRecordsByType_success() throws Exception {
        when(recordService.getRecordsByType("INCOME")).thenReturn(List.of(testDto));

        mockMvc.perform(get("/api/records/type/INCOME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("INCOME"));
    }

    @Test
    void getRecordsByCategory_success() throws Exception {
        when(recordService.getRecordsByCategory("Sales")).thenReturn(List.of(testDto));

        mockMvc.perform(get("/api/records/category/Sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Sales"));
    }
}
