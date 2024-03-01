package com.example.storageservice.repository;

import com.example.storageservice.model.Storage;
import com.example.storageservice.model.enums.StorageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StorageRepository extends JpaRepository<Storage, Long> {

}
