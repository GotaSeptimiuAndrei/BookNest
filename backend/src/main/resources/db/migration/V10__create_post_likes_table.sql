CREATE TABLE IF NOT EXISTS post_likes (
      user_id BIGINT NOT NULL,
      post_id BIGINT NOT NULL,
      liked_at DATETIME NOT NULL,
      PRIMARY KEY (user_id, post_id),
      CONSTRAINT fk_user_likes
          FOREIGN KEY (user_id) REFERENCES users(user_id)
              ON DELETE CASCADE ON UPDATE CASCADE,
      CONSTRAINT fk_post_likes
          FOREIGN KEY (post_id) REFERENCES posts(post_id)
              ON DELETE CASCADE ON UPDATE CASCADE
);
