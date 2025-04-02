package backend.repository;

import backend.model.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentsRepository extends JpaRepository<PostComments, Long> {

	List<PostComments> findByPostPostId(Long postId);

	List<PostComments> findByParentCommentCommentId(Long parentCommentId);

}
