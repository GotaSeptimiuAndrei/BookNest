package backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

	@NotBlank(message = "Username is required.")
	private String username;

	@NotNull(message = "Book id is required.")
	private Long bookId;

	@Min(value = 1, message = "Min value for a review is 1.")
	@Max(value = 5, message = "Max value for a review is 5.")
	private Double rating;

	@Size(max = 1000, message = "Review description cannot exceed 1000 characters.")
	private String reviewDescription;

}
