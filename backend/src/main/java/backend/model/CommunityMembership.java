package backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_membership")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityMembership {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "membership_id")
	private Long membershipId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "community_id", nullable = false)
	private Community community;

	@Column(name = "joined_at", insertable = false, updatable = false)
	private LocalDateTime joinedAt;

}
