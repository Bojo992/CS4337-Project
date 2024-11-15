package com.cs4337.project.model;

/***
 * The category of messages. Most are CHAT, but messages of type CONNECT and DISCONNECT are used for messages sent when a user joins and leaves respectively.
 */
public enum MessageType {
    CHAT,
    CONNECT,
    DISCONNECT
}
