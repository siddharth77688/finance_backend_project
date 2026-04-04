package com.finance.demo.service;

import com.finance.demo.dto.CreateUserRequest;
import com.finance.demo.dto.FinancialRecordDto;
import com.finance.demo.dto.UserDto;
import com.finance.demo.entity.FinancialRecord;
import com.finance.demo.entity.Role;
import com.finance.demo.entity.User;
import com.finance.demo.exception.ResourceNotFoundException;
import com.finance.demo.exception.UserAlreadyExistsException;
import com.finance.demo.repository.FinancialRecordRepository;
import com.finance.demo.repository.RoleRepository;
import com.finance.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role adminRole;
    private Role viewerRole;

    @BeforeEach
    void setUp() {
        adminRole = Role.builder().id(1L).name(Role.RoleName.ADMIN).build();
        viewerRole = Role.builder().id(2L).name(Role.RoleName.VIEWER).build();

        testUser = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@finance.com")
                .password("encodedPassword")
                .firstName("System")
                .lastName("Administrator")
                .active(true)
                .roles(new HashSet<>(Set.of(adminRole)))
                .build();
    }

    @Test
    void createUser_withRoles_success() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("new@finance.com")
                .password("pass123")
                .firstName("New")
                .lastName("User")
                .roles(Set.of("ADMIN"))
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@finance.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ADMIN)).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_withoutRoles_defaultsToViewer() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("new@finance.com")
                .password("pass123")
                .firstName("New")
                .lastName("User")
                .roles(null)
                .build();

        User savedUser = User.builder()
                .id(2L).username("newuser").email("new@finance.com")
                .password("enc").firstName("New").lastName("User")
                .active(true).roles(new HashSet<>(Set.of(viewerRole))).build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@finance.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.VIEWER)).thenReturn(Optional.of(viewerRole));
        when(passwordEncoder.encode("pass123")).thenReturn("enc");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(request);

        assertTrue(result.getRoles().contains("VIEWER"));
    }

    @Test
    void createUser_withEmptyRoles_defaultsToViewer() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("new@finance.com")
                .password("pass123")
                .roles(Collections.emptySet())
                .build();

        User savedUser = User.builder()
                .id(2L).username("newuser").email("new@finance.com")
                .password("enc").active(true).roles(new HashSet<>(Set.of(viewerRole))).build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@finance.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.VIEWER)).thenReturn(Optional.of(viewerRole));
        when(passwordEncoder.encode("pass123")).thenReturn("enc");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(request);

        assertTrue(result.getRoles().contains("VIEWER"));
    }

    @Test
    void createUser_usernameExists_throwsException() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("admin").email("new@finance.com").password("pass").build();

        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
    }

    @Test
    void createUser_emailExists_throwsException() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser").email("admin@finance.com").password("pass").build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("admin@finance.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
    }

    @Test
    void createUser_roleNotFound_throwsException() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser").email("new@finance.com").password("pass")
                .roles(Set.of("ADMIN")).build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@finance.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ADMIN)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.createUser(request));
    }

    @Test
    void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getUsername());
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(1L);

        assertEquals("admin", result.getUsername());
        assertEquals("admin@finance.com", result.getEmail());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void updateUser_withPasswordAndRoles() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("updated").email("updated@finance.com")
                .password("newpass").firstName("Up").lastName("Dated")
                .roles(Set.of("VIEWER")).build();

        User updatedUser = User.builder()
                .id(1L).username("updated").email("updated@finance.com")
                .password("encodedNew").firstName("Up").lastName("Dated")
                .active(true).roles(new HashSet<>(Set.of(viewerRole))).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(Role.RoleName.VIEWER)).thenReturn(Optional.of(viewerRole));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNew");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, request);

        assertEquals("updated", result.getUsername());
        assertTrue(result.getRoles().contains("VIEWER"));
    }

    @Test
    void updateUser_withoutPassword() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("updated").email("updated@finance.com")
                .password(null).firstName("Up").lastName("Dated")
                .roles(null).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.updateUser(1L, request);

        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateUser_withEmptyPassword() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("updated").email("updated@finance.com")
                .password("").firstName("Up").lastName("Dated")
                .roles(null).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, request);

        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateUser_notFound() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("x").email("x@x.com").password("p").build();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(99L, request));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L));
    }

    @Test
    void getUserRecords_success() {
        FinancialRecord record = FinancialRecord.builder()
                .id(1L).title("Q1 Sales").description("desc")
                .type(FinancialRecord.RecordType.INCOME)
                .amount(new BigDecimal("50000"))
                .transactionDate(LocalDate.of(2024, 1, 15))
                .category("Sales").reference("REF-001")
                .createdBy(testUser)
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        when(financialRecordRepository.findByCreatedById(1L)).thenReturn(List.of(record));

        List<FinancialRecordDto> result = userService.getUserRecords(1L);

        assertEquals(1, result.size());
        assertEquals("Q1 Sales", result.get(0).getTitle());
        assertEquals("admin", result.get(0).getCreatedBy());
    }

    @Test
    void getCurrentUser_withUserDetailsPrincipal() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));

        User result = userService.getCurrentUser();

        assertEquals("admin", result.getUsername());
    }

    @Test
    void getCurrentUser_withStringPrincipal() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("admin");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));

        User result = userService.getCurrentUser();

        assertEquals("admin", result.getUsername());
    }

    @Test
    void getCurrentUser_notFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("unknown");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUser());
    }
}
