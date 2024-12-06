package com.cs4337.mediastorage.controller;

import com.cs4337.mediastorage.model.ImgRequest;
import com.cs4337.mediastorage.service.MediaService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
public class MediaController {
    private MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/uploadObj")
    public Map<String, String> uploadObject(@RequestBody ImgRequest objName) throws Exception {
        var filename = UUID.randomUUID().toString();

        try {
            mediaService.putObjectIntoBucket(filename, objName.getBase64Data());
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }

        return Map.of("filename", filename);
    }
}
