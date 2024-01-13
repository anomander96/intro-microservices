package com.example.resourceprocessor.service;

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

    public Metadata extractMetadata(File mp3File) {
        Metadata metadata = null;
        try (InputStream inputStream = new FileInputStream(mp3File)) {
            ContentHandler handler = new BodyContentHandler(10 * 1024 * 1024);
            metadata = new Metadata();
            Mp3Parser mp3Parser = new Mp3Parser();
            ParseContext parseContext = new ParseContext();
            mp3Parser.parse(inputStream, handler, metadata, parseContext);
        } catch (IOException | SAXException | TikaException e) {
            log.error(e.getMessage());
            // add a proper handling in future
        }
        return metadata;
    }

    // in future should be added a processor which will storage metadata using the Song Service API.
}
