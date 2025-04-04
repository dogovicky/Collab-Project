package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.exception.TechnicalException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;


    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")); //Auto detect type (Image, Video etc)
            return uploadResult.get("secure_url").toString(); //Return file url
        } catch (IOException ex) {
            throw new TechnicalException("Failed to upload to cloudinary", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
