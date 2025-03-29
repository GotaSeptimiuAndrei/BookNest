package backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorSignupRequest {

	@NotBlank(message = "Full Name is required.")
	private String fullName;

	@NotBlank(message = "Email is required.")
	private String email;

	@NotBlank(message = "Password is required.")
	private String password;

	@NotNull(message = "Date of Birth is required.")
	private LocalDate dateOfBirth;

	@NotBlank(message = "City is required.")
	private String city;

	@NotBlank(message = "Country is required.")
	private String country;

	@NotBlank(message = "Bio is required.")
	private String bio;

	@NotNull(message = "Photo is required.")
	private MultipartFile photo;

}
