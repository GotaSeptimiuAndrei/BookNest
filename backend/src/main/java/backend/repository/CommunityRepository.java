package backend.repository;

import backend.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {

	Optional<Community> findByAuthorAuthorId(Long authorId);

	boolean existsByAuthorAuthorId(Long authorId);

}
