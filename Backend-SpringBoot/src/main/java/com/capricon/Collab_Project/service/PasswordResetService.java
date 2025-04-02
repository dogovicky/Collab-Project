package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.exception.ValidationException;
import com.capricon.Collab_Project.model.PasswordResetToken;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.repository.PasswordResetTokenRepo;
import com.capricon.Collab_Project.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class PasswordResetService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final PasswordResetTokenRepo resetTokenRepo;

    public PasswordResetService(UserRepo userRepo, PasswordEncoder passwordEncoder, MailService mailService,
                       PasswordResetTokenRepo resetTokenRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.resetTokenRepo = resetTokenRepo;
    }


    public void requestPasswordReset(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserException("User not registered"));

        //Generate unique reset token
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(Duration.ofMinutes(15));

        //Save token in database
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiry);
        resetTokenRepo.save(resetToken);

        //Send reset email
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        mailService.sendResetPasswordLink(email, resetLink, String.valueOf(expiry));

    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepo.findByToken(token)
                .orElseThrow(() -> new ValidationException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new ValidationException("Reset token has expired, try re-sending another request.");
        }

        //Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        resetTokenRepo.delete(resetToken);

    }

}
