CREATE TABLE IF NOT EXISTS posts (
     post_id BIGINT AUTO_INCREMENT PRIMARY KEY,
     community_id BIGINT NOT NULL,
     author_id BIGINT NOT NULL,
     text LONGTEXT NOT NULL,
     image VARCHAR(255),
     like_count INT DEFAULT 0,
     comment_count INT DEFAULT 0,
     date_posted DATETIME NOT NULL,
     CONSTRAINT fk_community_posts
         FOREIGN KEY (community_id) REFERENCES communities(community_id)
             ON DELETE CASCADE ON UPDATE CASCADE,
     CONSTRAINT fk_author_posts
         FOREIGN KEY (author_id) REFERENCES authors(author_id)
             ON DELETE CASCADE ON UPDATE CASCADE
);
