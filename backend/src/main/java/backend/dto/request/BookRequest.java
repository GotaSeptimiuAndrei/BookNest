package backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

	@NotBlank(message = "Image is required.")
	private String image;

}
