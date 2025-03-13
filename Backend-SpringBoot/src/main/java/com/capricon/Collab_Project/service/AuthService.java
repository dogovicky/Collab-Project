package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.SignUpRequest;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.UserAlreadyExistsException;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final Executor executor;

    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, MailService mailService, Executor executor) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.executor = executor;
    }


    public CompletableFuture<String> signUp(SignUpRequest request) {
        return CompletableFuture.supplyAsync(() -> signUpRequest(request))
                .exceptionally(ex -> {
                    log.error("Error during sign up for user {}: {}", request.getUsername(), ex.getMessage());
                    throw new RuntimeException("Sign up failed", ex);
                });
    }


    @Transactional
    private String signUpRequest(SignUpRequest request) {
        Optional<User> existingUser = userRepo.findByUsernameOrEmail(request.getUsername(), request.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .institution(request.getInstitution())
                .bio(request.getBio())
                .fieldOfInterest(request.getFieldOfInterest())
                .isEnabled(false)
                .verificationCode(mailService.generateVerificationCode())
                .build();

        userRepo.save(user);
        CompletableFuture.runAsync(() -> sendValidationEmail(user.getEmail(), user.getFullName()), executor);

        log.info("User registered successfully: username = {}, email = {}", user.getUsername(), user.getEmail());

        return "User registered successfully";

    }


    public void sendValidationEmail(String email, String fullName) {
        try {
            mailService.sendVerificationCode(email, fullName);
        } catch (BusinessException ex) {
            log.error("Failed to send verification email to {}: {}", email, ex.getMessage());
        }
    }


//    public boolean verifyCode(String email, String code) {
//
//    }

}
