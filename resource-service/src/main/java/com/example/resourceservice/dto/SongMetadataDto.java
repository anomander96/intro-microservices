package com.example.resourceservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SongMetadataDto {
    private String title;
    private String artist;
    private String album;
    private String genre;
}
