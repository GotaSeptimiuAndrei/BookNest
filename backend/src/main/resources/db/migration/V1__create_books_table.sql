CREATE TABLE IF NOT EXISTS Books (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     title VARCHAR(255) NOT NULL,
     author VARCHAR(255) NOT NULL,
     description TEXT,
     copies INT NOT NULL,
     copies_available INT NOT NULL,
     category VARCHAR(255) NOT NULL,
     image VARCHAR(255)
);
