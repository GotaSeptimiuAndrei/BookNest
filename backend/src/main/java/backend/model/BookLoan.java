package backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "book_loans")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookLoan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "loan_id")
	private Long loanId;

	@Column(name = "username")
	private String username;

	@Column(name = "book_id")
	private Long bookId;

	@Column(name = "loan_date")
	private LocalDate loanDate;

	@Column(name = "return_date")
	private LocalDate returnDate;

}
