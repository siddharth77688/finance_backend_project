package com.finance.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.demo.dto.CreateUserRequest;
import com.finance.demo.dto.FinancialRecordDto;
import com.finance.demo.dto.UserDto;
import com.finance.demo.security.JwtTokenProvider;
import com.finance.demo.service.UserService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto testUserDto;
    private CreateUserRequest createRequest;

    @BeforeEach
    void setUp() {
        testUserDto = UserDto.builder()
                .id(1L)
                .username("admin")
                .email("admin@finance.com")
                .firstName("System")
                .lastName("Administrator")
                .active(true)
                .roles(Set.of("ADMIN"))
                .build();

        createRequest = CreateUserRequest.builder()
                .username("newuser")
                .email("new@finance.com")
                .password("pass123")
                .firstName("New")
                .lastName("User")
                .roles(Set.of("ADMIN"))
                .build();
    }

    @Test
    void createUser_success() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(testUserDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@finance.com"));
    }

    @Test
    void getAllUsers_success() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(testUserDto));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    @Test
    void getUserById_success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUserDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void updateUser_success() throws Exception {
        UserDto updatedDto = UserDto.builder()
                .id(1L).username("updated").email("updated@finance.com")
                .firstName("Up").lastName("Dated").active(true)
                .roles(Set.of("VIEWER")).build();

        when(userService.updateUser(eq(1L), any(CreateUserRequest.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated"));
    }

    @Test
    void deleteUser_success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserRecords_success() throws Exception {
        FinancialRecordDto recordDto = FinancialRecordDto.builder()
                .id(1L).title("Q1 Sales").type("INCOME")
                .amount(new BigDecimal("50000"))
                .transactionDate(LocalDate.of(2024, 1, 15))
                .category("Sales").createdBy("admin")
                .createdAt("2024-01-15T10:00").build();

        when(userService.getUserRecords(1L)).thenReturn(List.of(recordDto));

        mockMvc.perform(get("/api/users/1/records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Q1 Sales"))
                .andExpect(jsonPath("$[0].createdBy").value("admin"));
    }
}
