package com.cs4337.mediastorage.controller;

import com.cs4337.mediastorage.model.ImgRequest;
import com.cs4337.mediastorage.service.MediaService;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class MediaController {
    MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping
    public void getBucketList() {
        List<Bucket> bucketList = mediaService.getBucketList();
        System.out.println("bucketList:"+bucketList);
    }

    @GetMapping("/downloadObj")
    public void downloadObject(@RequestParam("bucketName") String bucketName, @RequestParam("objName") String objName) throws Exception {
        mediaService.getObjectFromBucket(bucketName, objName);
    }

    @PostMapping("/uploadObj")
    public Map<String, String> uploadObject(@RequestParam("bucketName") String bucketName, @RequestBody ImgRequest objName) throws Exception {
        var filename = UUID.randomUUID().toString();

        try {
            mediaService.putObjectIntoBucket(bucketName, filename, objName.getBase64Data());
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }

        return Map.of("filename", filename);
    }

    @PostMapping("/createBucket")
    public String createBucket(@RequestParam("bucketName") String bucketName) {
        mediaService.createBucket(bucketName);
        return "done";
    }
}
