package com.project.service;

import com.project.model.JWTRefresh;
import com.project.repository.JWTRefreshRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JWTRefreshService {

    @Autowired
    private JWTRefreshRepository jwtRefreshRepository;

    public Optional<JWTRefresh> findByRefreshToken(String refreshToken){
        return jwtRefreshRepository.findByRefreshToken(refreshToken);
    }

    public void save(JWTRefresh jwtRefresh){
        jwtRefreshRepository.save(jwtRefresh);
    }

    public void delete(String refreshToken){
        jwtRefreshRepository.deleteByRefreshToken(refreshToken);
    }
}
