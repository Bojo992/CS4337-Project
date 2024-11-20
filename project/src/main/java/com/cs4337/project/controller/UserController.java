package com.cs4337.project.controller;

import com.cs4337.project.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/getUseId")
    public Map<String, Integer> getUseId(@RequestBody Map<String, String> map) {
        return userService.getUser(map.get("username"));
    }
}
