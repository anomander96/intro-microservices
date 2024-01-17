package com.example.resourceservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.resourceservice.model.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.resourceservice.service.ResourceService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @Operation(summary = "Upload the resource(audio file)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "File uploaded successfully.",
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
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Integer> uploadResource(@RequestParam("file") MultipartFile file) throws IOException {
        Integer resourceId = resourceService.uploadResource(file);

        if (resourceId != null) {
            return new ResponseEntity<>(resourceId, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Upload the resource(audio file) to S3 bucket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "File uploaded successfully.",
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
    @PostMapping(value = "/uploadToS3", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadResourceToS3(@RequestParam("file") MultipartFile file) {
        resourceService.uploadFileToS3Bucket(file);
        return new ResponseEntity<>("File uploaded successfully!", HttpStatus.OK);
    }

    @Operation(summary = "Get the binary audio data of the resource.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieves file.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Resource.class))
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
    @GetMapping("/{resourceId}")
    public ResponseEntity<Resource> getResourceById(@PathVariable("resourceId") Integer resourceId) {
        Resource resource = resourceService.getResourceById(resourceId);
        if (resource != null) {
            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete a resource(s). If there is no resource for id, do nothing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "File deleted successfully.",
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
    @DeleteMapping("/{resourceIds}")
    public ResponseEntity<Void> deleteResource(
            @RequestParam(value = "resourceIds") String resourceIds) {
        List<Integer> ids = Arrays.stream(resourceIds.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        resourceService.deleteResource(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
