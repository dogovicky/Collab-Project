package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.exception.ValidationException;
import com.capricon.Collab_Project.model.PasswordResetToken;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.repository.PasswordResetTokenRepo;
import com.capricon.Collab_Project.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final PasswordResetTokenRepo resetTokenRepo;
    private final Executor executor;

    //@Async("executor")
    public CompletableFuture<ApiResponse<String>> requestEmail(String email) {
//        SecurityContext context = SecurityContextHolder.getContext();
//        SecurityContextHolder.setContext(context);
        return CompletableFuture.supplyAsync(() -> requestPasswordReset(email), executor)
                .exceptionally(this::handlePasswordResetException);
    }

    //@Async("executor")
    public CompletableFuture<ApiResponse<String>> savePassword(String token, String newPassword) {
//        SecurityContext context = SecurityContextHolder.getContext();
//        SecurityContextHolder.setContext(context);
        return CompletableFuture.supplyAsync(() -> resetPassword(token, newPassword), executor)
                .exceptionally(this::handlePasswordResetException);
    }

    @Transactional
    public ApiResponse<String> requestPasswordReset(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserException("User not registered", HttpStatus.NOT_FOUND));

        //Generate unique reset token
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(Duration.ofMinutes(15));

        //Save token in database
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiry);
        resetTokenRepo.save(resetToken);

        try {
            //Send reset email
            String resetLink = "http://localhost:5173/reset-password?token=" + token;
            mailService.sendResetPasswordLink(email, resetLink, String.valueOf(expiry));
            return ApiResponse.success("Password reset email sent.");

        } catch (Exception ex) {
            log.error("Error processing password reset process: {}", ex.getMessage());
            throw new BusinessException("Failed to process email sending", HttpStatus.BAD_REQUEST);
        }
    }


    @Transactional
    public ApiResponse<String> resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepo.findByToken(token)
                .orElseThrow(() -> new ValidationException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (resetToken.isExpired()) {
            throw new ValidationException("Reset token has expired, try re-sending another request.", HttpStatus.BAD_REQUEST);
        }

        //Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        resetTokenRepo.delete(resetToken);
        return ApiResponse.success("Password has been successfully saved.");
    }


    public <T> ApiResponse<T> handlePasswordResetException(Throwable ex) {
        Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;
        log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);
        assert cause != null;
        return switch (cause) {
            case UserException userException ->
                    ApiResponse.error(userException.getStatus(), userException.getMessage());
            case BusinessException businessException ->
                    ApiResponse.error(businessException.getStatus(), businessException.getMessage());
            case ValidationException validationException ->
                    ApiResponse.error(validationException.getStatus(), validationException.getMessage());
            default -> ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
        };
    }

}
