package backend.repository;

import backend.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	Optional<Review> findByUsernameAndBookId(String username, Long bookId);

	void deleteAllByBookId(Long bookId);

	Page<Review> findAllByBookId(Long bookId, Pageable pageable);

	boolean existsByUsernameAndBookId(String username, Long bookId);

}
