CREATE DATABASE IF NOT EXISTS securityDB;

USE securityDB;

CREATE TABLE IF NOT EXISTS jwt_refresh (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       refresh_token VARCHAR(255),
                                       current_token VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS users (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 username VARCHAR(255),
                                 password VARCHAR(255),
                                 profile_picture VARCHAR(255),
                                 roles VARCHAR(255),
                                 status_message VARCHAR(255),
                                 email VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS chat (
                                 chat_id INT,
                                 user_id INT,
                                 is_admin BIT
    );

CREATE TABLE IF NOT EXISTS chat_info (
                                 chat_id INT,
                                 chat_name VARCHAR(255)
    );