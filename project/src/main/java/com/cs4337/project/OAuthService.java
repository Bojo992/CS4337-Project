package com.example.demo;

import org.springframework.beans.factoy.annotation.Value;
import org.springframework.sterotype.Service;


@Service
public class OAuthService {
    @Value("${oauth.client.id]")
    private String clientID;
    @Value("${oauth.client.secret}")
    private String clientSecret;
    @Value("${oauth.access.token.url}")
    private string accessToken;
    @Value("${oauth.client.auth.url}")
    private String clientAuthURL;


    public void printDetails(){
        System.out.println(clientID);
        System.out.println(clientSecret);
        System.out.println(accessToken);
        System.out.println(clientAuthURL);
    }
}