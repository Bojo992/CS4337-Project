package com.example.demo;

import com.example.demo.controller.NotificationController;
import com.example.demo.model.NotificationRequest;
import com.example.demo.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
        // Build MockMvc with the properly initialized notificationController
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    void testSendNotification() throws Exception {
        // Arrange
        NotificationRequest request = new NotificationRequest("user@example.com", "Test Subject", "Test Message");
        doNothing().when(notificationService).sendEmail(any(NotificationRequest.class));

        // Act & Assert
        mockMvc.perform(post("/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"user@example.com\", \"subject\": \"Test Subject\", \"message\": \"Test Message\"}"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).sendEmail(any(NotificationRequest.class));
    }
}
