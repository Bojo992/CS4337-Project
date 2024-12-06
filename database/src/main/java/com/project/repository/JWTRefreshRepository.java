package com.project.repository;

import com.project.model.JWTRefresh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JWTRefreshRepository extends JpaRepository<JWTRefresh, Long> {

    Optional<JWTRefresh> findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);
}
