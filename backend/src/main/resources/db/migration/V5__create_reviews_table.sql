CREATE TABLE IF NOT EXISTS reviews (
   review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
   username VARCHAR(255) NOT NULL,
   date DATE NOT NULL,
   rating DOUBLE NOT NULL,
   book_id BIGINT NOT NULL,
   review_description TEXT,
   CONSTRAINT fk_review_user_username
       FOREIGN KEY (username)
           REFERENCES users(username),
   CONSTRAINT fk_review_book
       FOREIGN KEY (book_id)
           REFERENCES books(book_id)
);
