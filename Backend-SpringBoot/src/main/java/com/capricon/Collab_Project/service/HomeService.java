package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.AttachmentDTO;
import com.capricon.Collab_Project.dto.PostDTO;
import com.capricon.Collab_Project.dto.UserDTO;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.model.Post;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.repository.PostRepo;
import com.capricon.Collab_Project.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;

    public ApiResponse<List<PostDTO>> getHomeFeed(String username) {
        try {
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

            List<String> interests = user.getFieldOfInterest();
            if (interests.isEmpty()) {
                return ApiResponse.success(Collections.emptyList(), "No interests found");
            }

            List<Post> relevantPosts = postRepo.findPostsByLabel(interests);
            List<PostDTO> postDTOS = relevantPosts.stream().map(this::mapToDTO).toList();
            return ApiResponse.success(postDTOS, "Successfully fetched posts");
        } catch (Exception ex) {
            Throwable cause = (ex instanceof RuntimeException && ex.getCause() != null) ? ex.getCause() : ex;
            log.error("Error processing request, caused by: {}", cause.getMessage());

            if (cause instanceof UserException userException) {
                return ApiResponse.error(userException.getStatus(), userException.getMessage());
            } else {
                return  ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
            }
        }
    }


    private PostDTO mapToDTO(Post post) {
        PostDTO postDTO = PostDTO.builder()
                .id(post.getId())
                .label(post.getLabel())
                .content(post.getContent())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .repostCount(post.getRepostCount())
                .timestamp(post.getCreatedAt())
                .build();

        UserDTO userDTO = UserDTO.builder()
                .username(post.getAuthorId().getUsername())
                .build();
        postDTO.setAuthor(userDTO);

        List<AttachmentDTO> attachmentDTOS = post.getAttachments()
                .stream()
                .map(attachment -> AttachmentDTO.builder()
                        .fileUrl(attachment.getFileUrl())
                        .build())
                .toList();
        postDTO.setAttachments(attachmentDTOS);

        return postDTO;
    }

}
