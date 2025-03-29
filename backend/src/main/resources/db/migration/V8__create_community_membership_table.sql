CREATE TABLE IF NOT EXISTS community_membership (
    membership_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    community_id BIGINT NOT NULL,
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_membership
        FOREIGN KEY (user_id) REFERENCES users(user_id)
            ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_community_membership
        FOREIGN KEY (community_id) REFERENCES communities(community_id)
            ON DELETE CASCADE ON UPDATE CASCADE
);