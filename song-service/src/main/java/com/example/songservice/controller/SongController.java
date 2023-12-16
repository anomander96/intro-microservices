package com.example.songservice.controller;

import com.example.songservice.model.Song;
import com.example.songservice.service.SongService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    @Operation(summary = "Create a new song metadata record in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Song created successfully.",
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
    @PostMapping
    public ResponseEntity<Map<String, Integer>> createSong(@RequestBody Song song) {
        Song createdSong = songService.saveSong(song);
        return new ResponseEntity<>(Map.of("id", createdSong.getId()), HttpStatus.CREATED);
    }

    @Operation(summary = "Returns song metadata.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Song metadata returned successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404",
                    description = "Bad request. List of validation errors(s)",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Song.class))
                    )),
            @ApiResponse(responseCode = "500",
                    description = "An internal server error has occurred.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Song> getSong(@PathVariable Integer id) {
        Song song = songService.getSongById(id);
        if(song != null) {
            return new ResponseEntity<>(song, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

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
    @DeleteMapping
    public ResponseEntity<Void> deleteSongs(@RequestParam List<Integer> ids) {
        songService.deleteSongs(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
