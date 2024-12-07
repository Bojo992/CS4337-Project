package com.project.controller;

import com.project.model.JWTRefresh;
import com.project.service.JWTRefreshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/jwt")
public class JWTRefreshController {
    @Autowired
    private JWTRefreshService jwtRefreshService;

    @GetMapping("/get")
    public ResponseEntity<JWTRefresh> getToken(@RequestParam String token) {
        Optional<JWTRefresh> jwtRefresh = jwtRefreshService.findByRefreshToken(token);
        return jwtRefresh.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveToken(@RequestBody JWTRefresh jwtRefresh) {
        try {
            jwtRefreshService.save(jwtRefresh);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteToken(@RequestParam String token) {
        try {
            jwtRefreshService.delete(token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
