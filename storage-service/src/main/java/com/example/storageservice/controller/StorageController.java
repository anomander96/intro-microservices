package com.example.storageservice.controller;

import com.example.storageservice.model.Storage;
import com.example.storageservice.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/storages")
public class StorageController {

    private final StorageService storageService;

    @Operation(summary = "Returns a list of all available storages.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of storages.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Storage.class))
                    )),
            @ApiResponse(responseCode = "404",
                    description = "Bad request. List of validation errors(s)",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )),
            @ApiResponse(responseCode = "500",
                    description = "An internal server error has occurred.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    @GetMapping("/allStorages")
    public ResponseEntity<List<Storage>> getStorages() {
        return ResponseEntity.ok(storageService.getStorages());
    }

    @Operation(summary = "Creates a new storage.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Storage added successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404",
                    description = "Bad request. List of validation errors(s)",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )),
            @ApiResponse(responseCode = "500",
                    description = "An internal server error has occurred.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    @PostMapping("/add")
    public ResponseEntity<Long> createStorage(@RequestParam Storage storage) {
        Long storageId = storageService.saveStorage(storage);

        if (storageId != null) {
            return new ResponseEntity<>(storageId, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<List<Long>> deleteStorage(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(storageService.deleteStorages(ids));
    }
}
