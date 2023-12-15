package service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Resource;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.springframework.stereotype.Service;
import repository.ResourceRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public Integer uploadResource(byte[] fileData) {
        // Extracting metadata using Apache Tika
        Metadata metadata = extractMetadata(fileData);

        // Create a Resource object
        Resource resource = new Resource();
        resource.setFile(fileData);
        resource.setMetadata(metadata.toString()); // Storing metadata as string for demonstration purposes

        try {
            // Save the Resource entity in the database
            Resource savedResource = resourceRepository.save(resource);
            return savedResource.getId(); // Return the ID of the created resource
        } catch (Exception e) {
            // Handle exception or log error if the saving process fails
            e.printStackTrace();
            // Return null or throw a custom exception as needed
            return null;
        }
    }

    public Resource getResourceById(Integer resourceId) {
        log.info("Called getResourceById()");
        return resourceRepository.findById(resourceId).orElse(null);
    }

    public void deleteResource(List<Integer> resourceIds) {
        log.info("Called deleteResource()");
        resourceRepository.deleteAllByIdInBatch(resourceIds);
    }

    private Metadata extractMetadata(byte[] fileData) {
        Metadata metadata = new Metadata();

        try (InputStream stream = new ByteArrayInputStream(fileData)) {
            Parser parser = new AutoDetectParser();
            parser.parse(stream, new org.apache.tika.sax.BodyContentHandler(), metadata, new ParseContext());
        } catch (Exception e) {
            log.error("Error while parsing file.");
            // add a proper exception
            throw new RuntimeException(e);
        }
        return metadata;
    }

}
