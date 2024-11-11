package com.project.security.repository;

import com.project.security.model.JwtRefresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtRefreshRepository extends JpaRepository<JwtRefresh, Integer> {
    public Optional<JwtRefresh> findByRefreshToken(String token);
}
