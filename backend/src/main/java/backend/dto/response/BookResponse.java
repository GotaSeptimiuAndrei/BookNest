package backend.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {

	@NotNull(message = "Id is required.")
	private Long bookId;

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
