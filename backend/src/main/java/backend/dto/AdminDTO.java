package backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDTO {

	@NotBlank(message = "Email is required.")
	private String email;

	@NotBlank(message = "Password is required.")
	private String password;

	@NotBlank(message = "Username is required.")
	private String username;

}