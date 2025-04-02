package backend.repository;

import backend.model.PostLikes;
import backend.model.PostLikesId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikesRepository extends JpaRepository<PostLikes, PostLikesId> {

}
