package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.ProfileUpdateRequest;
import com.capricon.Collab_Project.dto.UserProfileDTO;
import com.capricon.Collab_Project.exception.BaseException;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.TechnicalException;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.model.UserPrincipal;
import com.capricon.Collab_Project.model.enums.Gender;
import com.capricon.Collab_Project.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Slf4j
@Service
public class UserService {

    private final UserRepo userRepo;
    private final Executor executor;

    public UserService(UserRepo userRepo, Executor executor) {
        this.userRepo = userRepo;
        this.executor = executor;
    }

    @Async("executor")
    public CompletableFuture<UserProfileDTO> fetchProfileData(String username) {

        log.info("Fetching profile data asynchronously for user: {} ", username);

        CompletableFuture<UserProfileDTO> future = new CompletableFuture<>();
        try {
            UserProfileDTO profileData = getProfileData(username);
            log.info("Profile data retrieved for user: {}", username);
            future.complete(profileData);
        } catch (Exception ex) {
            log.error("An error occurred during fetching of user: {} ", username);
            future.completeExceptionally(ex);
        }
        return future;
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getProfileData(String username) {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UserException("User not found"));
        return new UserProfileDTO(user);
    }

    public CompletableFuture<UserProfileDTO> updateUserProfile(ProfileUpdateRequest request) {
        UUID userId = getAuthenticatedUserId();
        return CompletableFuture.supplyAsync(() ->
                updateProfile(userId, request), executor)
                .exceptionally(ex -> {
                    // Safely get the most specific cause
                    Throwable cause = NestedExceptionUtils.getMostSpecificCause(ex);

                    // Log with null check for messages
                    log.error("Failed to update user profile for user {}: {}", userId,
                            cause.getMessage());

                    // Handle the exception based on type
                    if (cause instanceof UserException || cause instanceof BusinessException
                            || cause instanceof TechnicalException) {
                        throw new CompletionException(cause);
                    } else {
                        // Default case, wrap in BaseException
                        throw new BaseException("Profile update failed: " +
                                cause.getMessage());
                    }
                });
    }


    @Transactional
    public UserProfileDTO updateProfile(UUID userId, ProfileUpdateRequest request) {
        User user = userRepo.findById(userId).orElseThrow(() -> new UserException("User not found: " + userId));

        updateIfNotNull(user::setUsername, request.getUsername());
        updateIfNotNull(user::setEmail, request.getEmail());
        updateIfNotNull(user::setFullName, request.getFullName());
        updateIfNotNull(user::setBio, request.getBio());
        updateIfNotNull(user::setInstitution, request.getInstitution());
        updateIfNotNull(user::setPhoneNumber, request.getPhoneNumber());

        try {
            if (request.getGender() != null && !request.getGender().isEmpty()) {
                user.setGender(Gender.valueOf(request.getGender()));
            }
        } catch (IllegalArgumentException ex) {
            throw new UserException("Invalid gender value");
        }

        if (request.getFieldsOfInterest() != null && !request.getFieldsOfInterest().isEmpty()) {
            user.setFieldOfInterest(request.getFieldsOfInterest());
        }

        userRepo.save(user);
        return new UserProfileDTO(user);
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UserException("User not authenticated");
        }

        return ((UserPrincipal) authentication.getPrincipal()).getUuid();
    }

    private void updateIfNotNull(Consumer<String> setter, String newValue) {
        if (newValue != null && !newValue.isEmpty()) {
            setter.accept(newValue);
        }
    }

}
