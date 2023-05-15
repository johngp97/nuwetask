DROP DATABASE IF EXISTS hackathon_backend_db;
CREATE DATABASE IF NOT EXISTS hackathon_backend_db;
USE hackathon_backend_db;

CREATE TABLE IF NOT EXISTS Users(
	users_id INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR (255) NOT NULL,
	user_password CHAR(60) NOT NULL
);

-- Users

INSERT INTO Users (username, user_password)
VALUES
("john", "123"),
("johngarcia", "123");
