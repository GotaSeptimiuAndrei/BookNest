package backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentRequest {

	@NotNull(message = "Post id is required.")
	private Long postId;

	@NotBlank(message = "Text is required.")
	private String text;

	private Long parentCommentId;

}
