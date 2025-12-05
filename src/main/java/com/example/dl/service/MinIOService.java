package com.example.dl.service;

import com.example.dl.config.MinIOConfig;
import com.example.dl.entity.FileEntity;
import com.example.dl.repository.FileRepository;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinIOService {
    
    private final MinioClient minioClient;
    private final MinIOConfig minIOConfig;
    private final FileRepository fileRepository;
    
    public FileEntity uploadFile(MultipartFile file) throws Exception {
        String originalName = file.getOriginalFilename();
        String extension = getFileExtension(originalName);
        String storedName = UUID.randomUUID().toString() + extension;
        String objectName = storedName;
        
        ensureBucketExists();
        
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minIOConfig.getBucketName())
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
        }

        FileEntity fileEntity = FileEntity.builder()
                .originalName(originalName)
                .storedName(storedName)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .extension(extension)
                .bucketName(minIOConfig.getBucketName())
                .objectName(objectName)
                .build();
        
        return fileRepository.save(fileEntity);
    }
    


    
    private void ensureBucketExists() throws Exception {
        boolean bucketExists = minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(minIOConfig.getBucketName())
                .build()
        );
        
        if (!bucketExists) {
            minioClient.makeBucket(
                MakeBucketArgs.builder()
                    .bucket(minIOConfig.getBucketName())
                    .build()
            );
            log.info("Created bucket: {}", minIOConfig.getBucketName());
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }
}

