package backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentResponse {

	private Long commentId;

	private Long postId;

	private Long commenterId; // user_id or author_id

	private String commenterName; // username or fullName

	private String commenterType; // "USER" | "AUTHOR"

	private String text;

	private LocalDateTime datePosted;

	private Long parentCommentId;

	// Children (replies) in a nested list
	private List<PostCommentResponse> replies = new ArrayList<>();

}
