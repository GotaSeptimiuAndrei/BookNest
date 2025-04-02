package backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

	private String authorFullName;

	private String communityName;

	private String text;

	private String imageUrl;

	private int likeCount;

	private int commentCount;

	private LocalDateTime datePosted;

}
