package com.cs4337.mediastorage.service;

import com.amazonaws.services.s3.model.Bucket;
import java.io.IOException;
import java.util.List;

public interface IBucketService {
    List<Bucket> getBucketList();

    boolean validateBucket(String bucketName);

    void getObjectFromBucket(String bucketName, String objectName) throws IOException;

    void putObjectIntoBucket(String bucketName, String objectName, String filePathToUpload);

    void createBucket(String bucket);
}
