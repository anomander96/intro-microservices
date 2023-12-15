package controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ResourceService;

import java.util.List;

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
            @ApiResponse(responseCode = "400",
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
    @PostMapping(value = "/upload")
    public ResponseEntity<Integer> uploadResource(@RequestBody byte[] fileData) {
        Integer resourceId = resourceService.uploadResource(fileData);
        // add a proper handling in service layer
        if (resourceId != null) {
            return new ResponseEntity<>(resourceId, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Retrieves the audio file based on provided id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieves file.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Resource.class))
                    )),
            @ApiResponse(responseCode = "400",
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
    public ResponseEntity<Resource> getResourceById(@PathVariable Integer resourceId) {
        Resource resource = resourceService.getResourceById(resourceId);
        if (resource != null) {
            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deletes the audio file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "File deleted successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400",
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
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable List<Integer> resourceIds) {
        resourceService.deleteResource(resourceIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
