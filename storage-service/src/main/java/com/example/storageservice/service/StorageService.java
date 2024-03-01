package com.example.storageservice.service;

import com.example.storageservice.model.Storage;
import com.example.storageservice.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;

    public Long saveStorage(Storage storage) {
        Storage savedStorage = storageRepository.save(storage);
        return savedStorage.getId();
    }

    public List<Storage> getStorages() {
        return storageRepository.findAll();
    }

    public List<Long> deleteStorages(List<Long> ids) {
        storageRepository.deleteAllById(ids);
        return ids;
    }
}
