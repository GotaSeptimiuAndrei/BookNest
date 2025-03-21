package backend.repository;

import backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	Optional<Review> findByUsernameAndBookId(String username, Long bookId);

	void deleteAllByBookId(Long bookId);

}
