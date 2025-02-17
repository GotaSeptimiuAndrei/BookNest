package backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Books")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_id")
	private Long bookId;

	@Column(name = "title")
	private String title;

	@Column(name = "author")
	private String author;

	@Column(name = "description")
	private String description;

	@Column(name = "copies")
	private int copies;

	@Column(name = "copies_available")
	private int copiesAvailable;

	@Column(name = "category")
	private String category;

	@Column(name = "image")
	private String image;

}
