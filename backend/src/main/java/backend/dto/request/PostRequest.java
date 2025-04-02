package backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

	@NotNull(message = "Community id is required.")
	private Long communityId;

	@NotNull(message = "Author id is required.")
	private Long authorId;

	@NotBlank(message = "Text is required.")
	private String text;

	private MultipartFile image;

}
