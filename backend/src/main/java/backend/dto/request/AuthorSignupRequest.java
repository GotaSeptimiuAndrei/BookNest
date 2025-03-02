package backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorSignupRequest {

	private String fullName;

	private String email;

	private String password;

	private LocalDate dateOfBirth;

	private String city;

	private String country;

	private String bio;

	private String photo;

}
