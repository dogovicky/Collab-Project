package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.SignUpRequest;
import com.capricon.Collab_Project.dto.ValidationRequest;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.exception.ValidationException;
import com.capricon.Collab_Project.model.Attachment;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.model.enums.AttachmentType;
import com.capricon.Collab_Project.model.enums.Gender;
import com.capricon.Collab_Project.repository.UserRepo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepo userRepo;
    private final Validator validator;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtService jwtService;
    private final Executor executor;
    private final RabbitMQPublisher publisher;
    private final TransactionTemplate transactionTemplate;
    private final CloudinaryService cloudinaryService;

    private void validateRequest(SignUpRequest request) {
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public CompletableFuture<ApiResponse<String>> signUp(SignUpRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            validateRequest(request);
            ApiResponse<String> message = transactionTemplate.execute(status -> signUpRequest(request));

            //Publish UserDTO as event payload
            publisher.sendMessage("notifications", "notifications.key", request);
            return message;
        }).exceptionally(ex -> {
            Throwable cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
            log.error("Error signing up user {}, caused by {}", request.getEmail(), cause.getMessage());

            if (cause instanceof UserException userException) {
                return ApiResponse.error(userException.getStatus(), userException.getMessage());
            } else if (cause instanceof BusinessException businessException) {
                return ApiResponse.error(businessException.getStatus(), businessException.getMessage());
            } else {
                return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, cause.getMessage());
            }
        });
    }


    @Transactional
    private ApiResponse<String> signUpRequest(SignUpRequest request) {
        Optional<User> existingUser = userRepo.findByUsernameOrEmail(request.getUsername(), request.getEmail());
        if (existingUser.isPresent()) {
            throw new UserException("User already exists", HttpStatus.CONFLICT);
        }

        String verificationCode = mailService.generateVerificationCode();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFirstName().concat(request.getLastName()))
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender() != null ? Gender.valueOf(request.getGender()) : null)
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .institution(request.getInstitution() != null ? request.getInstitution() : null)
                .bio(request.getBio() != null ? request.getBio() : null)
                .fieldOfInterest(request.getFieldOfInterest() != null ? request.getFieldOfInterest() : new ArrayList<>())
                .isEnabled(false)
                .verificationCode(verificationCode)
                .build();

//        AttachmentType fileType = getFileType(request.getProfilePic());
//        String file_url = cloudinaryService.uploadFile(request.getProfilePic());
//
//        Attachment attachment = Attachment.builder()
//                .user(user)
//                .fileUrl(file_url)
//                .fileType(fileType)
//                .build();
//
//        user.setProfilePictureUrl(file_url);

        try {
            mailService.sendVerificationCode(request.getEmail(), request.getFirstName().concat(request.getLastName()), verificationCode);
        } catch (Exception ex) {
            log.error("Failed to send email verification to {}: {}", request.getEmail(), ex.getMessage());
            throw new BusinessException("Failed to send verification email.", HttpStatus.BAD_REQUEST);
        }

        userRepo.save(user);
        log.info("User registered successfully: username = {}, email = {}", user.getUsername(), user.getEmail());
        return ApiResponse.success(user.getUsername(), "Sign up process successfully completed, check your email for account verification");
    }


    public CompletableFuture<ApiResponse<String>> verifyAccountByCode(ValidationRequest request) {
        return CompletableFuture.supplyAsync(() -> verifyAccount(request), executor)
                .exceptionally(ex -> {
                    Throwable cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
                    log.error("Failed to validate account {}: {}", request.getUsername(), ex.getMessage());
                    if (cause instanceof UserException userException) {
                        return ApiResponse.error(userException.getStatus(), userException.getMessage());
                    } else if (cause instanceof ValidationException validationException) {
                        return ApiResponse.error(validationException.getStatus(), validationException.getMessage());
                    } else {
                        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
                    }
                });
    }

    @Transactional
    public ApiResponse<String> verifyAccount(ValidationRequest request) {
        // Find user
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserException("User not yet registered", HttpStatus.NOT_FOUND));

        if (!user.getVerificationCode().equals(request.getCode())) {
            throw new ValidationException("Invalid verification code", HttpStatus.BAD_REQUEST);
        }

        user.setIsEnabled(true);
        user.setVerificationCode(null);
        userRepo.save(user);

        String token = jwtService.generateToken(request.getUsername());

        return ApiResponse.success(token, "Account successfully verified");
    }

    public AttachmentType getFileType(MultipartFile file) {
        String contentType = file.getContentType();

        List<String> imageTypes = Arrays.asList("image/png", "image/jpg", "image/jpeg", "image/gif");

        if (imageTypes.contains(contentType)) {
            return AttachmentType.IMAGE;
        } else {
            throw new IllegalArgumentException("Unsupported file format");
        }
    }

}
