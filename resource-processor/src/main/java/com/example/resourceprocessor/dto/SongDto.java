package com.example.resourceprocessor.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SongDto {
    private String name;
    private String artist;
    private String album;
    private String genre;
    private Integer year;
    private String duration;
}
