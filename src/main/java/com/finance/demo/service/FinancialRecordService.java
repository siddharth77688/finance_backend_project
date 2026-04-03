package com.finance.demo.service;

import com.finance.demo.dto.FinancialRecordDto;
import com.finance.demo.entity.FinancialRecord;
import com.finance.demo.entity.User;
import com.finance.demo.exception.ResourceNotFoundException;
import com.finance.demo.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FinancialRecordService {

    // Injecting the repository to perform CRUD operations on financial records
    private final FinancialRecordRepository recordRepository;
    private final UserService userService;

    public FinancialRecordDto createRecord(FinancialRecordDto dto) {
        User currentUser = userService.getCurrentUser();

        FinancialRecord record = FinancialRecord.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .type(FinancialRecord.RecordType.valueOf(dto.getType()))
                .amount(dto.getAmount())
                .transactionDate(dto.getTransactionDate())
                .category(dto.getCategory())
                .reference(dto.getReference())
                .createdBy(currentUser)
                .build();

        FinancialRecord saved = recordRepository.save(record);
        return mapToDto(saved);
    }

    public List<FinancialRecordDto> getAllRecords() {
        return recordRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public FinancialRecordDto getRecordById(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));
        return mapToDto(record);
    }

    public FinancialRecordDto updateRecord(Long id, FinancialRecordDto dto) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        record.setTitle(dto.getTitle());
        record.setDescription(dto.getDescription());
        record.setType(FinancialRecord.RecordType.valueOf(dto.getType()));
        record.setAmount(dto.getAmount());
        record.setTransactionDate(dto.getTransactionDate());
        record.setCategory(dto.getCategory());
        record.setReference(dto.getReference());

        FinancialRecord updated = recordRepository.save(record);
        return mapToDto(updated);
    }

    public void deleteRecord(Long id) {
        if (!recordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Record not found with id: " + id);
        }
        recordRepository.deleteById(id);
    }

    public List<FinancialRecordDto> getRecordsByType(String type) {
        return recordRepository.findByType(FinancialRecord.RecordType.valueOf(type)).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<FinancialRecordDto> getRecordsByCategory(String category) {
        return recordRepository.findByCategory(category).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private FinancialRecordDto mapToDto(FinancialRecord record) {
        return FinancialRecordDto.builder()
                .id(record.getId())
                .title(record.getTitle())
                .description(record.getDescription())
                .type(record.getType().name())
                .amount(record.getAmount())
                .transactionDate(record.getTransactionDate())
                .category(record.getCategory())
                .reference(record.getReference())
                .createdBy(record.getCreatedBy().getUsername())
                .createdAt(record.getCreatedAt().toString())
                .build();
    }
}
