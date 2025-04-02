package backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Long postId;

	@ManyToOne
	@JoinColumn(name = "community_id", nullable = false)
	private Community community;

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	private Author author;

	@Column(name = "text", nullable = false, columnDefinition = "LONGTEXT")
	private String text;

	@Column(name = "image")
	private String image;

	@Column(name = "like_count")
	private Integer likeCount;

	@Column(name = "comment_count")
	private Integer commentCount;

	@Column(name = "date_posted", nullable = false)
	private LocalDateTime datePosted;

}
