package backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLikesId implements Serializable {

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "post_id")
	private Long postId;

}
