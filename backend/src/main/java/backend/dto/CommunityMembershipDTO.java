package backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityMembershipDTO {

	@NotNull(message = "Community id is required.")
	private Long communityId;

	@NotNull(message = "User id is required.")
	private Long userId;

}
