package com.example.dl.repository;

import com.example.dl.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    
    Optional<FileEntity> findByStoredName(String storedName);
    
    Optional<FileEntity> findByObjectName(String objectName);
    
    boolean existsByStoredName(String storedName);
}
