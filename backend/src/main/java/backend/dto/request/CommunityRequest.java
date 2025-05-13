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
public class CommunityRequest {

	@NotNull(message = "Author id is required.")
	private Long authorId;

	@NotBlank(message = "Name is required.")
	private String name;

	@NotBlank(message = "Description is required.")
	private String description;

	@NotNull(message = "Photo is required.")
	private MultipartFile photo;

}
