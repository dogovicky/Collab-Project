package com.capricon.Collab_Project.dto;

import com.capricon.Collab_Project.model.enums.AttachmentType;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AttachmentDTO {
    private UUID id;
    private String fileUrl;
    private AttachmentType attachmentType;
}
