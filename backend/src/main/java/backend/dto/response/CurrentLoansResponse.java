package backend.dto.response;

import backend.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentLoansResponse {

	private Book book;

	private int daysLeft;

}
