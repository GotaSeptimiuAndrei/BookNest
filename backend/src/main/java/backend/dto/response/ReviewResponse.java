package backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

	private Long reviewId;

	private String username;

	private Long bookId;

	private Double rating;

	private String reviewDescription;

	private LocalDate date;

}
