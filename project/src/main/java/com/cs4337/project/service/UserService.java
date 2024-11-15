package com.cs4337.project.service;

import com.cs4337.project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<String, Integer> getUser(String username) {
        var user = userRepository.getByUsername(username);

        if (user.isPresent()) {
            return Map.of("userId", user.get().getId());
        }

        return Map.of("userId", 0);
    }
}
