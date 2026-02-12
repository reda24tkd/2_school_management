CREATE DATABASE IF NOT EXISTS school_management;

USE school_management;

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    age INT NOT NULL,
    grade VARCHAR(1) NOT NULL
);

INSERT INTO students (name, email, age, grade) VALUES
('Ahmed Alami', 'ahmed@school.com', 20, 'A'),
('Sara Bennani', 'sara@school.com', 19, 'B'),
('Youssef Idrissi', 'youssef@school.com', 21, 'A'),
('Fatima Zahra', 'fatima@school.com', 20, 'B');
