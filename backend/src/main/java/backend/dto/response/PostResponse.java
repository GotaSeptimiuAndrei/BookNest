package backend.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.annotations.NotNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

	@NotNull
	private Long postId;

	@NotBlank
	private String authorFullName;

	@NotBlank
	private String communityName;

	private String text;

	private String imageUrl;

	@NotNull
	private int likeCount;

	@NotNull
	private int commentCount;

	@NotNull
	private LocalDateTime datePosted;

	private boolean likedByMe;

}
