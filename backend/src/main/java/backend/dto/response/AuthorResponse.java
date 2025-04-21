package backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorResponse {

	private Long authorId;

	private String fullName;

	private String email;

	private LocalDate dateOfBirth;

	private String city;

	private String country;

	private String bio;

	private String photo;

}
