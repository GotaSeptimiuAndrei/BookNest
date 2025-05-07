package backend.repository;

import backend.model.Book;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	List<Book> findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(String title, String author);

	Page<Book> findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(String title, String author,
			Pageable pageable);

	Page<Book> findByCategoryIgnoreCaseAndTitleIgnoreCaseContainingOrCategoryIgnoreCaseAndAuthorIgnoreCaseContaining(
			String category1, String title, String category2, String author, Pageable pageable);

	@Nonnull
	Page<Book> findAll(@Nonnull Pageable pageable);

}
