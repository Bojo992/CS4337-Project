package com.project.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.security.model.Users;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService {
    ObjectMapper objectMapper = new ObjectMapper();
    String baseUrl = "ec2-52-212-119-177.eu-west-1.compute.amazonaws.com:8082";


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String param2 = URLEncoder.encode(username, StandardCharsets.UTF_8);

            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(new URI(baseUrl + "/users?email=" + param2))
                    .GET()
                    .build();

            Optional<Users> myUserOptional = Optional.of(null);

            // Send the request and get the response
            HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                var temp = objectMapper.readValue(response.body(), Users.class);

                myUserOptional = Optional.of(temp);
            }

            if (myUserOptional.isPresent()) {
                Users myUser = myUserOptional.get();
                return User.builder()
                        .username(myUser.getUsername())
                        .password(myUser.getPassword())
                        .roles(myUser.getRoles().split(","))
                        .build();
            } else {
                throw new UsernameNotFoundException("User " + username + " was not found");
            }
        } catch (Exception ex) {
            throw new UsernameNotFoundException(ex.getMessage());
        }
    }

    public Map<String, Object> registerUser(HashMap<String, String> request) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String param2 = URLEncoder.encode(request.get("email").toString(), StandardCharsets.UTF_8);

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
                return Map.of("Error", "User with this username or email already exist");
            }

            String password = new BCryptPasswordEncoder().encode(request.get("password"));
            Users user1 = new Users().builder()
                    .username(request.get("username"))
                    .password(password)
                    .email(request.get("email"))
                    .roles("USER")
                    .build();

            var jsonBody = "{\"email\": \"" + user1.getEmail() + " ,\"password\":" + user1.getPassword() + ", \"name\": " + user1.getUsername() + ",\"profilePicture\": " + user1.getProfilePicture() + ",\"statusMessage\": " + user1.getStatusMessage() + "}";

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(new URI(baseUrl + "/users/create"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            client.send(request2, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            return Map.of("Success", "User registered successfully");
        }
    }
}
