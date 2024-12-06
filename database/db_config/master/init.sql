CREATE DATABASE IF NOT EXISTS chat_app_db;

CREATE USER 'replica'@'%' IDENTIFIED BY 'replicapassword';
GRANT REPLICATION SLAVE ON *.* TO 'replica'@'%';
FLUSH PRIVILEGES;

USE chat_app_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(32) UNIQUE NOT NULL,
    password VARCHAR(32) NOT NULL,
    name VARCHAR(32) NOT NULL,
    profile_picture VARCHAR(255) NOT NULL,
    status_message TEXT
);

CREATE TABLE IF NOT EXISTS contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_one_id BIGINT NOT NULL,
    user_two_id BIGINT NOT NULL,
    is_blocked BOOLEAN NOT NULL,
    FOREIGN KEY (user_one_id) REFERENCES users(id),
    FOREIGN KEY (user_two_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS chats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_name VARCHAR(16) NOT NULL,
    is_group BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT,
    media VARCHAR(255),
    sent_at DATETIME NOT NULL,
    is_seen BOOLEAN,
    FOREIGN KEY (chat_id) REFERENCES chats(id),
    FOREIGN KEY (sender_id) REFERENCES users(id),
    CONSTRAINT CHK_message CHECK (message IS NOT NULL OR media IS NOT NULL)
);

CREATE TABLE IF NOT EXISTS chat_members (
    user_id BIGINT,
    chat_id BIGINT,
    role ENUM('Admin', 'Member'),
    PRIMARY KEY (user_id, chat_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (chat_id) REFERENCES chats(id)
);