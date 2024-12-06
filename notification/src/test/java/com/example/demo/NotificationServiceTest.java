package com.example.demo;

import com.example.demo.model.NotificationRequest;
import com.example.demo.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    public NotificationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendEmail() {
        // Arrange
        NotificationRequest request = new NotificationRequest("user@example.com", "Test Subject", "Test Message");

        // Act
        notificationService.sendEmail(request);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
