package com.project.security.repository;

import com.project.security.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyUserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
}
