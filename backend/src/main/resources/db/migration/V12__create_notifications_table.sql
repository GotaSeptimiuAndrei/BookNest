CREATE TABLE IF NOT EXISTS notifications (
     notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
     user_id BIGINT NOT NULL,
     message VARCHAR(255) NOT NULL,
     timestamp DATETIME NOT NULL,
     read_status BOOLEAN NOT NULL DEFAULT FALSE,
     CONSTRAINT fk_notifications_user
         FOREIGN KEY (user_id) REFERENCES users(user_id)
             ON DELETE CASCADE ON UPDATE CASCADE
);
