package com.example.resourceprocessor.service;

import com.example.resourceprocessor.dto.ResourceDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class MetadataExtractionService {

    public Metadata extractMetadata(ResourceDTO resource) {
        Metadata metadata = new Metadata();

        metadata.add("dc:title", resource.getName());
        metadata.add("xmpDM:artist", resource.getArtist());
        metadata.add("xmpDM:album", resource.getAlbum());
        metadata.add("xmpDM:audioCompressor", resource.getGenre());
        metadata.add("xmpDM:releaseDate", String.valueOf(resource.getYear()));
        metadata.add("xmpDM:duration", resource.getDuration());

        return metadata;
    }

    // in future should be added a processor which will storage metadata using the Song Service API.
}
