package backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostComments {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long commentId;

	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "parent_comment_id")
	private PostComments parentComment;

	@Column(name = "text", nullable = false)
	private String text;

	@Column(name = "date_posted", nullable = false)
	private LocalDateTime datePosted;

}
