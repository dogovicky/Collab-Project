package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.ApiResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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


    @Async
    public CompletableFuture<ApiResponse<UserProfileDTO>> fetchProfileData(String username) {
        log.info("SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication());
        return CompletableFuture.completedFuture(getProfileData(username))
                .exceptionally(this::handleServiceProfileException);
    }

    @Transactional(readOnly = true)
    public ApiResponse<UserProfileDTO> getProfileData(String username) {
        User user = userRepo.findProfileDataByUsername(username)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        return ApiResponse.success(new UserProfileDTO(user));
    }

    @Async
    public CompletableFuture<ApiResponse<UserProfileDTO>> updateUserProfile(ProfileUpdateRequest request) {
        UUID userId = getAuthenticatedUserId();
        return CompletableFuture.completedFuture(updateProfile(userId, request))
                .exceptionally(this::handleServiceProfileException);
    }


    @Transactional
    public ApiResponse<UserProfileDTO> updateProfile(UUID userId, ProfileUpdateRequest request) {
        // Find user
        User user = userRepo.findById(userId).orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

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
            throw new UserException("Invalid gender value", HttpStatus.BAD_REQUEST);
        }

        if (request.getFieldsOfInterest() != null && !request.getFieldsOfInterest().isEmpty()) {
            user.setFieldOfInterest(request.getFieldsOfInterest());
        }

        userRepo.save(user);
        return ApiResponse.success(new UserProfileDTO(user));
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UserException("User not authenticated", HttpStatus.UNAUTHORIZED);
        }

        return ((UserPrincipal) authentication.getPrincipal()).getUuid();
    }

    private void updateIfNotNull(Consumer<String> setter, String newValue) {
        if (newValue != null && !newValue.isEmpty()) {
            setter.accept(newValue);
        }
    }


    public <T> ApiResponse<T> handleServiceProfileException(Throwable ex) {
        Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

        log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);

        if (cause instanceof UserException userException) {
            return ApiResponse.error(userException.getStatus(), userException.getMessage());
        } else if (cause instanceof IllegalArgumentException argumentException) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, argumentException.getMessage());
        } else {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
        }
    }

}
