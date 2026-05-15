-- Run this on any machine to get the database ready
DROP DATABASE IF EXISTS wordy;
CREATE DATABASE wordy;
USE wordy;

CREATE TABLE players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('PLAYER', 'ADMIN') DEFAULT 'PLAYER',
    wins INT DEFAULT 0
);

CREATE TABLE leaderboard_words (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    word VARCHAR(100) NOT NULL,
    word_length INT NOT NULL

);

CREATE TABLE time_config (
    id INT PRIMARY KEY DEFAULT 1,
    waiting_time_seconds INT DEFAULT 10,
    round_duration_seconds INT DEFAULT 30
);

INSERT INTO time_config (id, waiting_time_seconds, round_duration_seconds)
VALUES (1, 10, 30);

INSERT INTO players (username, password, role)
VALUES ('admin', '1234', 'ADMIN');

INSERT INTO players (username, password, role)
VALUES ('player1', '1234', 'PLAYER');

SELECT 'Database setup complete.' AS Status;
