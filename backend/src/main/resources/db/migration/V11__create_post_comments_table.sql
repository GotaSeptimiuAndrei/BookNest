CREATE TABLE IF NOT EXISTS post_comments (
     comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
     post_id BIGINT NOT NULL,
     user_id BIGINT NOT NULL,
     parent_comment_id BIGINT NULL,
     text VARCHAR(255) NOT NULL,
     date_posted DATETIME NOT NULL,
     CONSTRAINT fk_post_comments
         FOREIGN KEY (post_id) REFERENCES posts(post_id)
             ON DELETE CASCADE ON UPDATE CASCADE,
     CONSTRAINT fk_user_comments
         FOREIGN KEY (user_id) REFERENCES users(user_id)
             ON DELETE CASCADE ON UPDATE CASCADE,
     CONSTRAINT fk_parent_comment
         FOREIGN KEY (parent_comment_id) REFERENCES post_comments(comment_id)
             ON DELETE CASCADE ON UPDATE CASCADE
);
