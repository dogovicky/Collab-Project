package com.capricon.Collab_Project.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {

    private String username;
    private String content;
    private String label;
    private UUID communityId;
    private List<MultipartFile> attachments; // Images / Videos

}
