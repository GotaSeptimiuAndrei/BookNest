package backend.repository;

import backend.model.Author;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

	Optional<Author> findByEmail(String email);

	@Nonnull
	Page<Author> findAll(@Nonnull Pageable pageable);

	Optional<Author> findByFullNameIgnoreCase(String fullName);

	Page<Author> findByFullNameIgnoreCaseContaining(String fullName, Pageable pageable);

}
