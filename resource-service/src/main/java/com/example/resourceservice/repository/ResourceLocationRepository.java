package com.example.resourceservice.repository;

import com.example.resourceservice.model.ResourceLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceLocationRepository extends JpaRepository<ResourceLocation, Integer> {
}
