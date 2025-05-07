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

	private String title;

	private String author;

	private String description;

	private int copies;

	private int copiesAvailable;

	private String category;

	private String image;

}
