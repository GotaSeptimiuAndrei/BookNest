package backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long reviewId;

	@Column(name = "username")
	private String username;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "rating")
	private Double rating;

	@Column(name = "book_id")
	private Long bookId;

	@Column(name = "review_description")
	private String reviewDescription;

}
