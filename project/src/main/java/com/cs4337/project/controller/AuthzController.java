package com.cs4337.project.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@RestController
public class AuthzController {
    static String API_URL = "http://localhost:8081";
   /* @PostMapping("/auth/login")
    public Map<String, Object> createUser(@RequestBody Map<String, String> fields)
    {
        return Map.of()
    }*/

    private static void insertRequest() {
        try {
            URL url = new URL(API_URL + "user");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            //String jsonInputString = String.format("{\"email\":\"%s_%d@gmail.com\"}",user, pass);
           // requestWithPayload(conn, jsonInputString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
