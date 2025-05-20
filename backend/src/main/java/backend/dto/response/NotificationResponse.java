package backend.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

	@NotNull
	private Long notificationId;

	@NotNull
	private Long communityId;

	@NotBlank
	private String message;

	@NotNull
	private LocalDateTime createdAt;

	@NotNull
	private boolean read;

}
