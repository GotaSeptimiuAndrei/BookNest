package backend.unit.serviceTests;

import backend.dto.response.CurrentLoansResponse;
import backend.exception.BookLoanException;
import backend.model.Book;
import backend.model.BookLoan;
import backend.repository.BookLoanRepository;
import backend.repository.BookRepository;
import backend.service.BookLoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

class BookLoanServiceTest {

	@Mock
	private BookLoanRepository bookLoanRepository;

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookLoanService bookLoanService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testLoanBook_Success() {
		String username = "testUser";
		Long bookId = 1L;

		Book book = new Book();
		book.setBookId(bookId);
		book.setCopiesAvailable(5);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookLoanRepository.findByUsernameAndBookId(username, bookId)).thenReturn(Optional.empty());

		BookLoan result = bookLoanService.loanBook(username, bookId);

		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo(username);
		assertThat(result.getBookId()).isEqualTo(bookId);
		assertThat(result.getLoanDate()).isEqualTo(LocalDate.now());
		assertThat(result.getReturnDate()).isEqualTo(LocalDate.now().plusDays(3));

		verify(bookRepository).findById(bookId);
		verify(bookLoanRepository).findByUsernameAndBookId(username, bookId);

		assertThat(book.getCopiesAvailable()).isEqualTo(4);
		verify(bookRepository).save(book);

		ArgumentCaptor<BookLoan> loanCaptor = ArgumentCaptor.forClass(BookLoan.class);
		verify(bookLoanRepository).save(loanCaptor.capture());
		BookLoan savedLoan = loanCaptor.getValue();
		assertThat(savedLoan.getUsername()).isEqualTo(username);
		assertThat(savedLoan.getBookId()).isEqualTo(bookId);
	}

	@Test
	void testLoanBook_BookNotFound() {
		String username = "testUser";
		Long bookId = 999L;

		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookLoanService.loanBook(username, bookId)).isInstanceOf(BookLoanException.class)
			.hasMessage("Book is not available or is already loaned by current user");

		verify(bookRepository).findById(bookId);
		verify(bookLoanRepository, never()).save(any());
	}

	@Test
	void testLoanBook_AlreadyLoaned() {
		String username = "testUser";
		Long bookId = 1L;

		Book book = new Book();
		book.setBookId(bookId);
		book.setCopiesAvailable(5);

		BookLoan existingLoan = new BookLoan();
		existingLoan.setUsername(username);
		existingLoan.setBookId(bookId);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookLoanRepository.findByUsernameAndBookId(username, bookId)).thenReturn(Optional.of(existingLoan));

		assertThatThrownBy(() -> bookLoanService.loanBook(username, bookId)).isInstanceOf(BookLoanException.class)
			.hasMessage("Book is not available or is already loaned by current user");

		verify(bookRepository).findById(bookId);
		verify(bookLoanRepository).findByUsernameAndBookId(username, bookId);
		verify(bookLoanRepository, never()).save(any());
	}

	@Test
	void testLoanBook_NoCopiesAvailable() {
		String username = "testUser";
		Long bookId = 1L;

		Book book = new Book();
		book.setBookId(bookId);
		book.setCopiesAvailable(0);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookLoanRepository.findByUsernameAndBookId(username, bookId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookLoanService.loanBook(username, bookId)).isInstanceOf(BookLoanException.class)
			.hasMessage("Book is not available or is already loaned by current user");

		verify(bookRepository).findById(bookId);
		verify(bookLoanRepository).findByUsernameAndBookId(username, bookId);
		verify(bookLoanRepository, never()).save(any());
	}

	@Test
	void testCurrentLoansCount() {
		String username = "testUser";

		List<BookLoan> mockLoans = List.of(new BookLoan(), new BookLoan());
		when(bookLoanRepository.findByUsername(username)).thenReturn(mockLoans);

		int count = bookLoanService.currentLoansCount(username);

		assertThat(count).isEqualTo(2);
		verify(bookLoanRepository).findByUsername(username);
	}

	@Test
	void testIsBookLoanedByUser() {
		String username = "testUser";
		Long bookId = 1L;

		when(bookLoanRepository.findByUsernameAndBookId(username, bookId)).thenReturn(Optional.of(new BookLoan()));

		boolean result = bookLoanService.isBookLoanedByUser(username, bookId);

		assertThat(result).isTrue();
		verify(bookLoanRepository).findByUsernameAndBookId(username, bookId);
	}

	@Test
	void testCurrentLoansByUser_Success() {
		String username = "testUser";

		// Setup BookLoan with a future return date
		BookLoan loan1 = new BookLoan();
		loan1.setBookId(1L);
		loan1.setReturnDate(LocalDate.now().plusDays(5));

		// Setup BookLoan with a past return date
		BookLoan loan2 = new BookLoan();
		loan2.setBookId(2L);
		loan2.setReturnDate(LocalDate.now().minusDays(1));

		when(bookLoanRepository.findByUsername(username)).thenReturn(List.of(loan1, loan2));

		Book book1 = new Book();
		book1.setBookId(1L);
		book1.setTitle("Book 1");

		Book book2 = new Book();
		book2.setBookId(2L);
		book2.setTitle("Book 2");

		when(bookRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(book1, book2));

		List<CurrentLoansResponse> result = bookLoanService.currentLoansByUser(username);

		// Only loan1 should be considered "current" because loan2's return date is in the
		// past
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getBook().getTitle()).isEqualTo("Book 1");
		// daysLeft should be about 5 if returned in the future
		assertThat(result.getFirst().getDaysLeft()).isEqualTo(5);

		verify(bookLoanRepository).findByUsername(username);
		verify(bookRepository).findAllById(List.of(1L, 2L));
	}

	@Test
	void testReturnBook_Success() {
		String username = "testUser";
		Long bookId = 1L;

		Book book = new Book();
		book.setBookId(bookId);
		book.setCopiesAvailable(5);

		BookLoan loan = new BookLoan();
		loan.setUsername(username);
		loan.setBookId(bookId);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookLoanRepository.findByUsernameAndBookId(username, bookId)).thenReturn(Optional.of(loan));

		bookLoanService.returnBook(username, bookId);

		assertThat(book.getCopiesAvailable()).isEqualTo(6);
		verify(bookRepository).save(book);
		verify(bookLoanRepository).delete(loan);
	}

	@Test
	void testReturnBook_NotLoaned() {
		String username = "testUser";
		Long bookId = 1L;

		Book book = new Book();
		book.setBookId(bookId);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookLoanRepository.findByUsernameAndBookId(username, bookId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookLoanService.returnBook(username, bookId)).isInstanceOf(BookLoanException.class)
			.hasMessage("Book is not available or is not loaned by current user");

		verify(bookRepository).findById(bookId);
		verify(bookLoanRepository).findByUsernameAndBookId(username, bookId);
		verify(bookLoanRepository, never()).delete(any());
	}

	@Test
	void testReturnBook_BookNotFound() {
		String username = "testUser";
		Long bookId = 999L;

		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookLoanService.returnBook(username, bookId)).isInstanceOf(BookLoanException.class)
			.hasMessage("Book is not available or is not loaned by current user");

		verify(bookRepository).findById(bookId);
		verify(bookLoanRepository, never()).delete(any());
	}

	@Test
	void testRenewLoan_Success() {
		String username = "testUser";
		Long bookId = 1L;

		BookLoan existingLoan = new BookLoan();
		existingLoan.setReturnDate(LocalDate.now().plusDays(2));

		when(bookLoanRepository.findByUsernameAndBookId(username, bookId)).thenReturn(Optional.of(existingLoan));

		bookLoanService.renewLoan(username, bookId);

		assertThat(existingLoan.getReturnDate()).isEqualTo(LocalDate.now().plusDays(3));

		verify(bookLoanRepository).save(existingLoan);
	}

	@Test
	void testRenewLoan_NotLoaned() {
		String username = "testUser";
		Long bookId = 1L;

		when(bookLoanRepository.findByUsernameAndBookId(username, bookId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookLoanService.renewLoan(username, bookId)).isInstanceOf(BookLoanException.class)
			.hasMessage("Book is not loaned by current user");

		verify(bookLoanRepository).findByUsernameAndBookId(username, bookId);
		verify(bookLoanRepository, never()).save(any());
	}

	@Test
	void testRenewLoan_Expired() {
		String username = "testUser";
		Long bookId = 1L;

		BookLoan existingLoan = new BookLoan();
		existingLoan.setReturnDate(LocalDate.now().minusDays(1)); // Already past

		when(bookLoanRepository.findByUsernameAndBookId(username, bookId)).thenReturn(Optional.of(existingLoan));

		assertThatThrownBy(() -> bookLoanService.renewLoan(username, bookId)).isInstanceOf(BookLoanException.class)
			.hasMessage("Cannot renew loan. The return date has passed.");

		verify(bookLoanRepository).findByUsernameAndBookId(username, bookId);
		verify(bookLoanRepository, never()).save(any());
	}

}
