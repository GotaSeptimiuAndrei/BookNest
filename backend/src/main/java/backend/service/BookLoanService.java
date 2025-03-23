package backend.service;

import backend.dto.response.CurrentLoansResponse;
import backend.exception.BookLoanException;
import backend.model.Book;
import backend.model.BookLoan;
import backend.repository.BookLoanRepository;
import backend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookLoanService {

	private final BookLoanRepository bookLoanRepository;

	private final BookRepository bookRepository;

	public BookLoan loanBook(String username, Long bookId) {
		Optional<Book> book = bookRepository.findById(bookId);

		Optional<BookLoan> validateLoan = bookLoanRepository.findByUsernameAndBookId(username, bookId);

		if (book.isEmpty() || validateLoan.isPresent() || book.get().getCopiesAvailable() <= 0) {
			throw new BookLoanException("Book is not available or is already loaned by current user");
		}

		book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
		bookRepository.save(book.get());

		BookLoan bookLoan = new BookLoan();
		bookLoan.setUsername(username);
		bookLoan.setBookId(bookId);
		bookLoan.setLoanDate(LocalDate.now());
		bookLoan.setReturnDate(LocalDate.now().plusDays(3));

		bookLoanRepository.save(bookLoan);

		return bookLoan;
	}

	public int currentLoansCount(String username) {
		return bookLoanRepository.findByUsername(username).size();
	}

	public boolean isBookLoanedByUser(String username, Long bookId) {
		return bookLoanRepository.findByUsernameAndBookId(username, bookId).isPresent();
	}

	public List<CurrentLoansResponse> currentLoansByUser(String username) {
		List<CurrentLoansResponse> currentLoans = new ArrayList<>();
		List<BookLoan> bookLoans = bookLoanRepository.findByUsername(username);

		List<Long> bookIds = bookLoans.stream().map(BookLoan::getBookId).toList();

		List<Book> books = bookRepository.findAllById(bookIds);

		LocalDate today = LocalDate.now();

		for (Book book : books) {
			Optional<BookLoan> bookLoan = bookLoans.stream()
				.filter(b -> b.getBookId().equals(book.getBookId()))
				.findFirst();

			if (bookLoan.isPresent()) {
				LocalDate returnDate = LocalDate.parse(bookLoan.get().getReturnDate().toString());
				if (returnDate.isAfter(today)) {
					int daysLeft = (int) ChronoUnit.DAYS.between(today, returnDate);
					currentLoans.add(new CurrentLoansResponse(book, daysLeft));
				}
			}
		}

		return currentLoans;
	}

	public void returnBook(String username, Long bookId) {
		Optional<Book> book = bookRepository.findById(bookId);
		Optional<BookLoan> validateLoan = bookLoanRepository.findByUsernameAndBookId(username, bookId);

		if (book.isEmpty() || validateLoan.isEmpty()) {
			throw new BookLoanException("Book is not available or is not loaned by current user");
		}

		book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);
		bookRepository.save(book.get());

		bookLoanRepository.delete(validateLoan.get());
	}

	public void renewLoan(String username, Long bookId) {
		Optional<BookLoan> bookLoan = bookLoanRepository.findByUsernameAndBookId(username, bookId);

		if (bookLoan.isEmpty()) {
			throw new BookLoanException("Book is not loaned by current user");
		}

		LocalDate today = LocalDate.now();
		LocalDate returnDate = LocalDate.parse(bookLoan.get().getReturnDate().toString());

		if (returnDate.isAfter(today)) {
			bookLoan.get().setReturnDate(LocalDate.now().plusDays(3));
			bookLoanRepository.save(bookLoan.get());
		}
		else
			throw new BookLoanException("Cannot renew loan. The return date has passed.");
	}

}
