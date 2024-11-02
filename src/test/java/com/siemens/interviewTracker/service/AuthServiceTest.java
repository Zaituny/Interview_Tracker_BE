//package com.siemens.interviewTracker.service;
//
//import com.siemens.interviewTracker.dto.UserDTO;
//import com.siemens.interviewTracker.entity.User;
//import com.siemens.interviewTracker.repository.UserRepository;
//import com.siemens.interviewTracker.mapper.UserMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import jakarta.validation.Validator;
//import jakarta.validation.ConstraintViolation;
//
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AuthServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private Validator validator;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @InjectMocks
//    private AuthService authService;
//
//    private UserDTO userDTO;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        userDTO = new UserDTO();
//        userDTO.setEmail("test@example.com");
//        userDTO.setPassword("Password@123");
//
//        user = new User();
//        user.setEmail(userDTO.getEmail());
//        user.setPassword("encodedPassword");
//    }
//
//    @Test
//    void testSignup_Success() {
//        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
//        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
//        when(userMapper.userDTOToUser(userDTO)).thenReturn(user);
//        when(userRepository.save(user)).thenReturn(user);
//
//        User createdUser = authService.signup(userDTO);
//
//        assertNotNull(createdUser);
//        assertEquals(user.getEmail(), createdUser.getEmail());
//        assertEquals("encodedPassword", createdUser.getPassword());
//        verify(userRepository, times(1)).save(user);
//    }
//
//    @Test
//    void testSignup_UserAlreadyExists() {
//        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            authService.signup(userDTO);
//        });
//
//        assertEquals("User already exists", exception.getMessage());
//        verify(userRepository, never()).save(any());
//    }
//
//    @Test
//    void testSignup_InvalidPassword() {
//        userDTO.setPassword("short");
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            authService.signup(userDTO);
//        });
//
//        assertTrue(exception.getMessage().contains("Password must be between 8 and 49 characters"));
//    }
//}
//
