package com.cs4337.project.service;

import com.cs4337.project.model.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    ObjectMapper objectMapper = new ObjectMapper();
    String baseUrl = "ec2-52-212-119-177.eu-west-1.compute.amazonaws.com:8082";

    public Map<String, Integer> getUser(String username) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String param2 = URLEncoder.encode(username, StandardCharsets.UTF_8);

            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(new URI(baseUrl + "/users?email=" + param2))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());

            Optional<Users> user = Optional.of(null);

            if (response.statusCode() == 200) {
                var temp = objectMapper.readValue(response.body(), Users.class);

                user = Optional.of(temp);
            }

            if (user.isPresent()) {
                return Map.of("userId", user.get().getId());
            }

            return Map.of("userId", 0);
        } catch (Exception e) {
            return Map.of("userId", 0);
        }
    }
}
