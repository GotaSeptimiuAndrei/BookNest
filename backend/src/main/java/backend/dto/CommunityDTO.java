package backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityDTO {

	@NotNull(message = "Author id is required.")
	private Long authorId;

	@NotBlank(message = "Name is required.")
	private String name;

	private String description;

	private MultipartFile photo;

}
