package backend.repository;

import backend.model.BookLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {

	Optional<BookLoan> findByUsernameAndBookId(String username, Long bookId);

	List<BookLoan> findByUsername(String username);

	void deleteAllByBookId(Long bookId);

}
