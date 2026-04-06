package com.finance.demo.config;

import com.finance.demo.entity.FinancialRecord;
import com.finance.demo.entity.Role;
import com.finance.demo.entity.User;
import com.finance.demo.repository.FinancialRecordRepository;
import com.finance.demo.repository.RoleRepository;
import com.finance.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final FinancialRecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRoles();
        initUsers();
        initSampleRecords();
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            Role viewer = Role.builder()
                    .name(Role.RoleName.VIEWER)
                    .description("Can view records and dashboard")
                    .build();

            Role analyst = Role.builder()
                    .name(Role.RoleName.ANALYST)
                    .description("Can view records and dashboard")
                    .build();

            Role admin = Role.builder()
                    .name(Role.RoleName.ADMIN)
                    .description("Full access to all features")
                    .build();

            roleRepository.save(viewer);
            roleRepository.save(analyst);
            roleRepository.save(admin);
        }
    }

    private void initUsers() {
        Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN).orElseThrow();
        Role viewerRole = roleRepository.findByName(Role.RoleName.VIEWER).orElseThrow();
        Role analystRole = roleRepository.findByName(Role.RoleName.ANALYST).orElseThrow();

        if (!userRepository.existsByUsername("admin")) {

            User admin = User.builder()
                    .username("admin")
                    .email("admin@finance.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("System")
                    .lastName("Administrator")
                    .active(true)
                    .roles(new HashSet<>(Set.of(adminRole)))
                    .build();

            User viewer = User.builder()
                    .username("viewer")
                    .email("viewer@finance.com")
                    .password(passwordEncoder.encode("viewer123"))
                    .firstName("View")
                    .lastName("Only")
                    .active(true)
                    .roles(new HashSet<>(Set.of(viewerRole)))
                    .build();

            User analyst = User.builder()
                    .username("analyst")
                    .email("analyst@finance.com")
                    .password(passwordEncoder.encode("analyst123"))
                    .firstName("Data")
                    .lastName("Analyst")
                    .active(true)
                    .roles(new HashSet<>(Set.of(analystRole)))
                    .build();

            userRepository.save(admin);
            userRepository.save(viewer);
            userRepository.save(analyst);
        }
    }

    private void initSampleRecords() {
        if (recordRepository.count() == 0) {
            User admin = userRepository.findByUsername("admin").orElseThrow();

            FinancialRecord record1 = FinancialRecord.builder()
                    .title("Q1 Sales Revenue")
                    .description("Sales revenue for first quarter")
                    .type(FinancialRecord.RecordType.INCOME)
                    .amount(new BigDecimal("50000.00"))
                    .transactionDate(LocalDate.now().minusDays(30))
                    .category("Sales")
                    .reference("REF-001")
                    .createdBy(admin)
                    .build();

            FinancialRecord record2 = FinancialRecord.builder()
                    .title("Office Rent")
                    .description("Monthly office rent payment")
                    .type(FinancialRecord.RecordType.EXPENSE)
                    .amount(new BigDecimal("5000.00"))
                    .transactionDate(LocalDate.now().minusDays(20))
                    .category("Operations")
                    .reference("REF-002")
                    .createdBy(admin)
                    .build();

            FinancialRecord record3 = FinancialRecord.builder()
                    .title("Software Licenses")
                    .description("Annual software license renewal")
                    .type(FinancialRecord.RecordType.EXPENSE)
                    .amount(new BigDecimal("12000.00"))
                    .transactionDate(LocalDate.now().minusDays(15))
                    .category("IT")
                    .reference("REF-003")
                    .createdBy(admin)
                    .build();

            FinancialRecord record4 = FinancialRecord.builder()
                    .title("Investment Returns")
                    .description("Returns from stock portfolio")
                    .type(FinancialRecord.RecordType.INVESTMENT)
                    .amount(new BigDecimal("8000.00"))
                    .transactionDate(LocalDate.now().minusDays(10))
                    .category("Investment")
                    .reference("REF-004")
                    .createdBy(admin)
                    .build();

            FinancialRecord record5 = FinancialRecord.builder()
                    .title("Q2 Sales Revenue")
                    .description("Sales revenue for second quarter")
                    .type(FinancialRecord.RecordType.INCOME)
                    .amount(new BigDecimal("65000.00"))
                    .transactionDate(LocalDate.now().minusDays(5))
                    .category("Sales")
                    .reference("REF-005")
                    .createdBy(admin)
                    .build();

            recordRepository.save(record1);
            recordRepository.save(record2);
            recordRepository.save(record3);
            recordRepository.save(record4);
            recordRepository.save(record5);
        }
    }
}
