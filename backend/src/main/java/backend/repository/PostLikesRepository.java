package backend.repository;

import backend.model.PostLikes;
import backend.model.PostLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface PostLikesRepository extends JpaRepository<PostLikes, PostLikesId> {

	@Query("""
			select pl.id.postId
			from PostLikes pl
			where pl.id.userId = :userId
			  and pl.id.postId in :postIds
			""")
	List<Long> findLikedPostIds(Long userId, Collection<Long> postIds);

}
