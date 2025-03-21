CREATE TABLE IF NOT EXISTS Reviews (
       review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
       username VARCHAR(255) NOT NULL,
       date DATE NOT NULL,
       rating DOUBLE NOT NULL,
       book_id BIGINT NOT NULL,
       review_description TEXT,
       CONSTRAINT fk_review_user_username
           FOREIGN KEY (username)
               REFERENCES Users(username),
       CONSTRAINT fk_review_book
           FOREIGN KEY (book_id)
               REFERENCES Books(book_id)
);
