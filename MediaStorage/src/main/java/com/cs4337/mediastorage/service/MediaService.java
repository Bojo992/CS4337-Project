package com.cs4337.mediastorage.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class MediaService implements IBucketService {
    private AmazonS3 s3Client;

    public MediaService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public List<Bucket> getBucketList() {
        return s3Client.listBuckets();
    }

    @Override
    public boolean validateBucket(String bucketName) {
        List<Bucket> bucketList = getBucketList();
        return bucketList.stream().anyMatch(m -> bucketName.equals(m.getName()));
    }

    @Override
    public void getObjectFromBucket(String bucketName, String objectName) throws IOException {
        S3Object s3Object = s3Client.getObject(bucketName, objectName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        FileOutputStream fos = new FileOutputStream(new File("opt/test/v1/"+objectName));
        byte[] read_buf = new byte[1024];
        int read_len = 0;
        while ((read_len = inputStream.read(read_buf)) > 0) {
            fos.write(read_buf, 0, read_len);
        }
        inputStream.close();
        fos.close();
    }

    @Override
    public void createBucket(String bucketName) {
        s3Client.createBucket(bucketName);
    }

    @Override
    public void putObjectIntoBucket(String bucketName, String objectName, String filePathToUpload) {
        try {
            var convertedFile =  convertBase64ToFile(filePathToUpload, objectName);
            s3Client.putObject(bucketName, objectName, convertedFile);
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
