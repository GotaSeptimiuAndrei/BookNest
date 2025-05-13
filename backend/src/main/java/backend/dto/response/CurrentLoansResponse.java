package backend.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentLoansResponse {

	@NotNull
	private BookResponse book;

	@NotNull
	private int daysLeft;

}
