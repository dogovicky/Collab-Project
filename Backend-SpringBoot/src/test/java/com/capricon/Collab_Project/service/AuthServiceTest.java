//package com.capricon.Collab_Project.service;
//
//import com.capricon.Collab_Project.dto.UserDTO;
//import com.capricon.Collab_Project.model.User;
//import com.capricon.Collab_Project.model.enums.Gender;
//import com.capricon.Collab_Project.repository.UserRepo;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.DataAccessException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.Optional;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executor;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
////@ExtendWith(MockitoExtension.class)
//public class AuthServiceTest {
//
//    @Mock
//    private UserRepo userRepo;
//
//    @Mock
//    private MailService mailService;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private Executor executor;
//
//    @InjectMocks
//    private AuthService authService; //The service being tested
//
//    //@Test
//    void shouldRegisterUserSuccessfully() throws ExecutionException, InterruptedException {
//
//        //Given
//        UserDTO request = new UserDTO();
//        request.setUsername("testUser");
//        request.setEmail("test@example.com");
//        request.setPassword("P@ssword123");
//        request.setGender(String.valueOf(Gender.MALE));
//        request.setFullName("Test User");
//        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
//        request.setPhoneNumber("12345678");
//        request.setInstitution("Test Institution");
//        request.setBio("Bio text");
//        request.setFieldOfInterest(Collections.singletonList("Software Development"));
//
//        User user = User.builder()
//                .username(request.getUsername())
//                .email(request.getEmail())
//                .password("hashedPassword")
//                .gender(Gender.valueOf(request.getGender()))
//                .isEnabled(false)
//                .verificationCode("123456")
//                .build();
//
//        //Stubbing behaviour
//        when(userRepo.findByUsernameOrEmail(request.getUsername(), request.getEmail())).thenReturn(Optional.empty());
//        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
//        when(userRepo.save(any(User.class))).thenReturn(user);
//        when(mailService.generateVerificationCode()).thenReturn("mockedCode");
//
//        //When
//        CompletableFuture<String> result = authService.signUp(request);
//
//        //Then
//        assertDoesNotThrow(() -> result.get()); //Ensure no exceptions
//        assertEquals("User registered successfully", result.join());
//
//        //verify interactions
//        verify(userRepo).save(any(User.class));
//        result.get();
//        verify(mailService).generateVerificationCode();
//    }
//
//    //@Test
//    void shouldThrowExceptionWhenUserAlreadyExists() {
//        //Given
//        UserDTO request = new UserDTO();
//        request.setUsername("testUser");
//        request.setEmail("test@example.com");
//
//        when(userRepo.findByUsernameOrEmail(request.getUsername(), request.getEmail()))
//                .thenReturn(Optional.of(new User()));
//
//        //When & then
//        CompletableFuture<String> result = authService.signUp(request);
//        assertThrows(ExecutionException.class, result::get);
//        verify(userRepo, never()).save(any(User.class));
//
//    }
//
//    //@Test
//    void shouldHandleDatabaseException() {
//        //Given
//        UserDTO request = new UserDTO();
//        request.setUsername("newUser");
//        request.setEmail("new@example.com");
//
//        when(userRepo.findByUsernameOrEmail(request.getUsername(), request.getEmail())).thenReturn(Optional.empty());
//        when(userRepo.save(any(User.class))).thenThrow(new DataAccessException("DB Error") {});
//
//        //When and Then
//        CompletableFuture<String> result = authService.signUp(request);
//        assertThrows(ExecutionException.class, result::get);
//
//    }
//
//}
