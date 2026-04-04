package com.finance.demo.service;

import com.finance.demo.dto.FinancialRecordDto;
import com.finance.demo.entity.FinancialRecord;
import com.finance.demo.entity.Role;
import com.finance.demo.entity.User;
import com.finance.demo.exception.ResourceNotFoundException;
import com.finance.demo.repository.FinancialRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialRecordServiceTest {

    @Mock
    private FinancialRecordRepository recordRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FinancialRecordService financialRecordService;

    private User testUser;
    private FinancialRecord testRecord;
    private FinancialRecordDto testDto;

    @BeforeEach
    void setUp() {
        Role adminRole = Role.builder().id(1L).name(Role.RoleName.ADMIN).build();
        testUser = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@finance.com")
                .password("encoded")
                .active(true)
                .roles(new HashSet<>(Set.of(adminRole)))
                .build();

        testRecord = FinancialRecord.builder()
                .id(1L)
                .title("Q1 Sales")
                .description("Sales revenue")
                .type(FinancialRecord.RecordType.INCOME)
                .amount(new BigDecimal("50000.00"))
                .transactionDate(LocalDate.of(2024, 1, 15))
                .category("Sales")
                .reference("REF-001")
                .createdBy(testUser)
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        testDto = FinancialRecordDto.builder()
                .title("Q1 Sales")
                .description("Sales revenue")
                .type("INCOME")
                .amount(new BigDecimal("50000.00"))
                .transactionDate(LocalDate.of(2024, 1, 15))
                .category("Sales")
                .reference("REF-001")
                .build();
    }

    @Test
    void createRecord_success() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(recordRepository.save(any(FinancialRecord.class))).thenReturn(testRecord);

        FinancialRecordDto result = financialRecordService.createRecord(testDto);

        assertEquals("Q1 Sales", result.getTitle());
        assertEquals("INCOME", result.getType());
        assertEquals("admin", result.getCreatedBy());
        verify(recordRepository).save(any(FinancialRecord.class));
    }

    @Test
    void getAllRecords_success() {
        when(recordRepository.findAll()).thenReturn(List.of(testRecord));

        List<FinancialRecordDto> result = financialRecordService.getAllRecords();

        assertEquals(1, result.size());
        assertEquals("Q1 Sales", result.get(0).getTitle());
    }

    @Test
    void getAllRecords_empty() {
        when(recordRepository.findAll()).thenReturn(List.of());

        List<FinancialRecordDto> result = financialRecordService.getAllRecords();

        assertTrue(result.isEmpty());
    }

    @Test
    void getRecordById_success() {
        when(recordRepository.findById(1L)).thenReturn(Optional.of(testRecord));

        FinancialRecordDto result = financialRecordService.getRecordById(1L);

        assertEquals("Q1 Sales", result.getTitle());
        assertEquals(1L, result.getId());
    }

    @Test
    void getRecordById_notFound() {
        when(recordRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> financialRecordService.getRecordById(99L));
    }

    @Test
    void updateRecord_success() {
        FinancialRecordDto updateDto = FinancialRecordDto.builder()
                .title("Updated Title")
                .description("Updated desc")
                .type("EXPENSE")
                .amount(new BigDecimal("30000.00"))
                .transactionDate(LocalDate.of(2024, 2, 1))
                .category("Operations")
                .reference("REF-UPD")
                .build();

        FinancialRecord updatedRecord = FinancialRecord.builder()
                .id(1L)
                .title("Updated Title")
                .description("Updated desc")
                .type(FinancialRecord.RecordType.EXPENSE)
                .amount(new BigDecimal("30000.00"))
                .transactionDate(LocalDate.of(2024, 2, 1))
                .category("Operations")
                .reference("REF-UPD")
                .createdBy(testUser)
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        when(recordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(recordRepository.save(any(FinancialRecord.class))).thenReturn(updatedRecord);

        FinancialRecordDto result = financialRecordService.updateRecord(1L, updateDto);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("EXPENSE", result.getType());
        assertEquals("Operations", result.getCategory());
    }

    @Test
    void updateRecord_notFound() {
        when(recordRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> financialRecordService.updateRecord(99L, testDto));
    }

    @Test
    void deleteRecord_success() {
        when(recordRepository.existsById(1L)).thenReturn(true);

        financialRecordService.deleteRecord(1L);

        verify(recordRepository).deleteById(1L);
    }

    @Test
    void deleteRecord_notFound() {
        when(recordRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> financialRecordService.deleteRecord(99L));
    }

    @Test
    void getRecordsByType_success() {
        when(recordRepository.findByType(FinancialRecord.RecordType.INCOME))
                .thenReturn(List.of(testRecord));

        List<FinancialRecordDto> result = financialRecordService.getRecordsByType("INCOME");

        assertEquals(1, result.size());
        assertEquals("INCOME", result.get(0).getType());
    }

    @Test
    void getRecordsByType_empty() {
        when(recordRepository.findByType(FinancialRecord.RecordType.TRANSFER))
                .thenReturn(List.of());

        List<FinancialRecordDto> result = financialRecordService.getRecordsByType("TRANSFER");

        assertTrue(result.isEmpty());
    }

    @Test
    void getRecordsByCategory_success() {
        when(recordRepository.findByCategory("Sales")).thenReturn(List.of(testRecord));

        List<FinancialRecordDto> result = financialRecordService.getRecordsByCategory("Sales");

        assertEquals(1, result.size());
        assertEquals("Sales", result.get(0).getCategory());
    }

    @Test
    void getRecordsByCategory_empty() {
        when(recordRepository.findByCategory("Unknown")).thenReturn(List.of());

        List<FinancialRecordDto> result = financialRecordService.getRecordsByCategory("Unknown");

        assertTrue(result.isEmpty());
    }
}
