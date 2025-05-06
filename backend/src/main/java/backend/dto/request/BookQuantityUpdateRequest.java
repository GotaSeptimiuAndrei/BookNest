package backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookQuantityUpdateRequest {

	@NotNull(message = "delta is required and must be either 1 or -1.")
	@Range(min = -1, max = 1, message = "delta must be -1 or 1.")
	private Integer delta;

}