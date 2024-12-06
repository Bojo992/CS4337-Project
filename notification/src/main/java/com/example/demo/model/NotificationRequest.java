package com.example.demo.model;

import java.util.Objects;

public class NotificationRequest {
    private String email;
    private String subject;
    private String message;

    // Default constructor
    public NotificationRequest() {}

    // All-args constructor
    public NotificationRequest(String email, String subject, String message) {
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // toString method
    @Override
    public String toString() {
        return "NotificationRequest{" +
                "email='" + email + '\'' +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    // Equals and HashCode methods for comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationRequest that = (NotificationRequest) o;
        return Objects.equals(email, that.email) &&
               Objects.equals(subject, that.subject) &&
               Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, subject, message);
    }
}
