package com.capricon.Collab_Project.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostDTO {
    private UUID id;
    private String content;
    private String label;
    private UserDTO author;
    private Timestamp timestamp;
    private List<AttachmentDTO> attachments;
    private int likeCount;
    private int repostCount;
    private int commentCount;
}
