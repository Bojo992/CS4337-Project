package com.cs4337.mediastorage.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class MediaService {
    private AmazonS3 s3Client;

    public MediaService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public void putObjectIntoBucket(String objectName, String filePathToUpload) {
        try {
            var convertedFile =  convertBase64ToFile(filePathToUpload, objectName);
            s3Client.putObject("", objectName, convertedFile);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    private File convertBase64ToFile(String base64Content, String filename) {
        byte[] decodedContent = Base64.getDecoder().decode(base64Content.getBytes(StandardCharsets.UTF_8));
        return bytesToFile(decodedContent, filename);
    }
    private File bytesToFile(byte[] content, String fileName) {
        File file = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        } catch (IOException e) {
            return null;
        }
        return file;
    }
}
