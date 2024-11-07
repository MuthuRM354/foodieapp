package com.foodieapp.user.service;

import com.foodieapp.user.model.Image;
import com.foodieapp.user.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageUploadService {

    @Autowired
    private ImageRepository imageRepository;

    public String uploadImage(MultipartFile file) {
        validateFile(file);
        try {
            Image image = new Image();
            image.setName(file.getOriginalFilename());
            image.setType(file.getContentType());
            image.setData(file.getBytes());
            image.setFileId(UUID.randomUUID().toString());
            image.setCategory("profile");

            Image savedImage = imageRepository.save(image);
            return savedImage.getFileId();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public String uploadDocument(MultipartFile file, Long userId) {
        validateFile(file);
        try {
            Image document = new Image();
            document.setName(file.getOriginalFilename());
            document.setType(file.getContentType());
            document.setData(file.getBytes());
            document.setFileId(UUID.randomUUID().toString());
            document.setUserId(userId);
            document.setCategory("document");

            Image savedDocument = imageRepository.save(document);
            return savedDocument.getFileId();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload document", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        if (file.getSize() > 5_000_000) {
            throw new RuntimeException("File size exceeds maximum limit of 5MB");
        }
    }

    public Image getImage(String fileId) {
        return imageRepository.findByFileId(fileId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    public void deleteImage(String fileId) {
        imageRepository.deleteByFileId(fileId);
    }
}
