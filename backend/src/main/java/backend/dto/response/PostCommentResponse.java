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

	private Long userId;

	private String username;

	private String text;

	private LocalDateTime datePosted;

	private Long parentCommentId;

	// Children (replies) in a nested list
	private List<PostCommentResponse> replies = new ArrayList<>();

}
