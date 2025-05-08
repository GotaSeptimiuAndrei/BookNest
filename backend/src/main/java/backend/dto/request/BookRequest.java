package backend.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {

	@NotBlank(message = "Title is required.")
	private String title;

	@NotBlank(message = "Author is required.")
	private String author;

	@NotBlank(message = "Description is required.")
	private String description;

	@Min(value = 0, message = "Copies cannot be negative.")
	private int copies;

	@Min(value = 0, message = "Available copies cannot be negative.")
	private int copiesAvailable;

	@NotBlank(message = "Category is required.")
	private String category;

	@NotNull(message = "Image is required.")
	private MultipartFile image;

	@AssertTrue(message = "Available copies cannot exceed total number of copies.")
	private boolean isCopiesAvailableValid() {
		return copiesAvailable <= copies;
	}

}
