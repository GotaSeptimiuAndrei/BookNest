CREATE TABLE authors (
     author_id BIGINT AUTO_INCREMENT PRIMARY KEY,
     full_name VARCHAR(255) NOT NULL UNIQUE,
     email VARCHAR(255) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL UNIQUE,
     date_of_birth DATE NOT NULL,
     city VARCHAR(100) NOT NULL,
     country VARCHAR(100) NOT NULL,
     bio TEXT NOT NULL,
     photo VARCHAR(255) NOT NULL
);
