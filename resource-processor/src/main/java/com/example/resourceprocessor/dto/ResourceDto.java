package com.example.resourceprocessor.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResourceDto {
    private Integer id;
    private byte[] file;
    private String fileLocation;
    private String name;
    private String artist;
    private String album;
    private String genre;
    private Integer year;
    private String duration;
}
