package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.DeletePostDTO;
import com.capricon.Collab_Project.dto.EventDTO;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.model.Attachment;
import com.capricon.Collab_Project.model.Community;
import com.capricon.Collab_Project.model.Post;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.model.enums.AttachmentType;
import com.capricon.Collab_Project.repository.AttachmentRepo;
import com.capricon.Collab_Project.repository.CommunityRepo;
import com.capricon.Collab_Project.repository.PostRepo;
import com.capricon.Collab_Project.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepo userRepo;
    private final CommunityRepo communityRepo;
    private final CloudinaryService cloudinaryService;
    private final AttachmentRepo attachmentRepo;
    private final PostRepo postRepo;
    private final Executor executor;

    // Fetch a post for a specific user (plus comments, likes and reposts)
    @Async
    public CompletableFuture<ApiResponse<Post>> getPost(String username) {
        return CompletableFuture.completedFuture(getPostByUsername(username))
                .exceptionally(this::handlePostException);
    }

    public ApiResponse<Post> getPostByUsername(String username) {
        // Ensure user account exists
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserException("User does not exists", HttpStatus.NOT_FOUND));

        // Fetch post
        Post post = postRepo.findPostByAuthorId(user)
                .orElseThrow(() -> new BusinessException("Post not found", HttpStatus.NOT_FOUND));

        return ApiResponse.success(post, "Post fetched successfully");
    }

    @Async
    public CompletableFuture<ApiResponse<Post>> createEvent(EventDTO eventDTO) {
        return CompletableFuture.completedFuture(saveEvent(eventDTO))
                .exceptionally(this::handlePostException);
    }

    @Async
    public CompletableFuture<AttachmentType> getFileType(MultipartFile file) {
        String contentType = file.getContentType();

        List<String> imageTypes = Arrays.asList("image/png", "image/jpg", "image/jpeg", "image/gif");
        List<String> videoTypes = Arrays.asList("video/mp4", "video/mpeg", "video/quicktime");

        if (imageTypes.contains(contentType)) {
            return CompletableFuture.completedFuture(AttachmentType.IMAGE);
        } else if (videoTypes.contains(contentType)) {
            return CompletableFuture.completedFuture(AttachmentType.VIDEO);
        } else {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Unsupported file format"));
        }
    }


    @Transactional
    public ApiResponse<Post> saveEvent(EventDTO eventDTO){

        // Find user
        User user = userRepo.findByUsername(eventDTO.getUsername())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        // Find the community (if provided)
        Community community = null;
        if (eventDTO.getCommunityId() != null) {
            community = communityRepo.findById(eventDTO.getCommunityId())
                    .orElseThrow(() -> new BusinessException("Community not found", HttpStatus.NOT_FOUND));
        }

        // Create and save post
        Post post = Post.builder()
                .content(eventDTO.getContent())
                .label(eventDTO.getLabel())
                .authorId(user)
                .communityId(community)
                .build();

        post = postRepo.save(post);

        // Save attachments if any
        List<Attachment> attachments = new ArrayList<>();
        if (eventDTO.getAttachments() != null) {
            for (MultipartFile file : eventDTO.getAttachments()) {
                CompletableFuture<AttachmentType> fileTypeFuture = getFileType(file);
                String file_url = cloudinaryService.uploadFile(file);

                try {
                    AttachmentType fileType = fileTypeFuture.get();
                    Attachment attachment = Attachment.builder()
                            .user(user)
                            .post(post)
                            .fileUrl(file_url)
                            .fileType(fileType)
                            .build();

                    attachments.add(attachment);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (!attachments.isEmpty()) {
            attachmentRepo.saveAll(attachments);
            post.setAttachments(attachments);
        }
        return ApiResponse.success(post, "Post saved successfully");
    }

    // Delete a particular post
    @Async
    public CompletableFuture<ApiResponse<Object>> deletePost(DeletePostDTO deletePostDTO) {
        return CompletableFuture.completedFuture(delete(deletePostDTO))
                .exceptionally(this::handlePostException);
    }

    @Transactional
    public ApiResponse<Object> delete(DeletePostDTO deletePostDTO) {
        // Find if user exists post
        User user = userRepo.findByUsername(deletePostDTO.getUsername())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        // Find if user owns post
        Post post = postRepo.findByAuthorIdAndId(user, deletePostDTO.getPostId())
                .orElseThrow(() -> new BusinessException("Can't delete post you don't own", HttpStatus.CONFLICT));

        // Delete post
        postRepo.delete(post);
        return ApiResponse.success(null, "Post successfully deleted");
    }

    public <T> ApiResponse<T> handlePostException(Throwable ex) {

        Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

        log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);

        if (cause instanceof UserException userException) {
            return ApiResponse.error(userException.getStatus(), userException.getMessage());
        } else if (cause instanceof BusinessException businessException) {
            return ApiResponse.error(businessException.getStatus(), businessException.getMessage());
        } else {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
        }
    }

}
