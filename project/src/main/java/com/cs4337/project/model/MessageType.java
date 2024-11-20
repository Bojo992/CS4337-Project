package com.cs4337.project.model;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MessageType {
    CHAT,
    CONNECT,
    DISCONNECT
}
