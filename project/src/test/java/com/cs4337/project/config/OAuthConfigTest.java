package com.cs4337.project.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OAuthConfigTest {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void testClientRegistrationRepository() {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");

        assertNotNull(clientRegistration);

        assertEquals("client_id", clientRegistration.getClientId());
        assertEquals("profile", clientRegistration.getScopes().toArray()[0]);
        assertEquals("email", clientRegistration.getScopes().toArray()[1]);
    }
}