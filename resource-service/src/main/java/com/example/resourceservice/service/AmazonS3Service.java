package com.example.resourceservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 s3Client;

    @Value("${S3_BUCKET_NAME}")
    private String bucketName;


    public void uploadFileToS3(MultipartFile file) {
        try {
            File fileObj = convertMultiPartToFile(file);
            s3Client.putObject(new PutObjectRequest(bucketName, file.getOriginalFilename(), fileObj));
            fileObj.delete();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public File getFileFromS3(String fileName) {
        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
        File file = new File(fileName);
        try(S3ObjectInputStream inputStream = object.getObjectContent()) {
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Error occurred while reading a file.");
        }
        return file;
    }

    public File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
