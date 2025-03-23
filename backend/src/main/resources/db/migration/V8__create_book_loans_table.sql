CREATE TABLE IF NOT EXISTS BookLoans (
  loan_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL,
  book_id BIGINT NOT NULL,
  loan_date DATE NOT NULL,
  return_date DATE NOT NULL,
  CONSTRAINT fk_loan_user_username
      FOREIGN KEY (username)
          REFERENCES Users(username),
  CONSTRAINT fk_loan_book_id
      FOREIGN KEY (book_id)
          REFERENCES Books(book_id)
);