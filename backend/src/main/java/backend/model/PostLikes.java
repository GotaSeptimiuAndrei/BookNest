package backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLikes {

	@EmbeddedId
	private PostLikesId id;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@MapsId("postId")
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	@Column(name = "liked_at", nullable = false)
	private LocalDateTime likedAt;

}
