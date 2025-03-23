package backend.unit.serviceTests;

import backend.dto.request.BookRequest;
import backend.dto.response.BookResponse;
import backend.exception.BookNotFoundException;
import backend.model.Book;
import backend.repository.BookLoanRepository;
import backend.repository.BookRepository;
import backend.repository.ReviewRepository;
import backend.service.BookService;
import backend.utils.converter.BookConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private BookLoanRepository bookLoanRepository;

	@InjectMocks
	private BookService bookService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetAllBooks() {
		Book book1 = new Book();
		book1.setBookId(1L);
		book1.setTitle("Title1");

		Book book2 = new Book();
		book2.setBookId(2L);
		book2.setTitle("Title2");

		when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

		List<BookResponse> result = bookService.getAllBooks();

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getTitle()).isEqualTo("Title1");
		assertThat(result.get(1).getTitle()).isEqualTo("Title2");

		verify(bookRepository).findAll();
	}

	@Test
	void testGetBookById_Found() {
		Book book = new Book();
		book.setBookId(1L);
		book.setTitle("Some Book Title");

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		BookResponse result = bookService.getBookById(1L);

		assertThat(result).isNotNull();
		assertThat(result.getBookId()).isEqualTo(1L);
		assertThat(result.getTitle()).isEqualTo("Some Book Title");
		verify(bookRepository).findById(1L);
	}

	@Test
	void testGetBookById_NotFound() {
		when(bookRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> bookService.getBookById(999L));
		verify(bookRepository).findById(999L);
	}

	@Test
	void testSaveBook() {
		BookRequest bookRequest = new BookRequest();
		bookRequest.setTitle("New Book");

		Book convertedEntity = BookConverter.convertToEntity(bookRequest);
		convertedEntity.setBookId(5L);

		when(bookRepository.save(any(Book.class))).thenReturn(convertedEntity);

		BookResponse result = bookService.saveBook(bookRequest);

		assertThat(result).isNotNull();
		assertThat(result.getBookId()).isEqualTo(5L);
		assertThat(result.getTitle()).isEqualTo("New Book");

		ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
		verify(bookRepository).save(bookCaptor.capture());
		Book savedBook = bookCaptor.getValue();
		assertThat(savedBook.getTitle()).isEqualTo("New Book");
	}

	@Test
	void testUpdateBook_Found() {
		Book existingBook = new Book();
		existingBook.setBookId(1L);
		existingBook.setTitle("Old Title");
		existingBook.setAuthor("Old Author");

		when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));

		BookRequest updatedDTO = new BookRequest();
		updatedDTO.setTitle("New Title");
		updatedDTO.setAuthor("New Author");

		Book updatedBook = new Book();
		updatedBook.setBookId(1L);
		updatedBook.setTitle("New Title");
		updatedBook.setAuthor("New Author");

		when(bookRepository.save(existingBook)).thenReturn(updatedBook);

		BookResponse result = bookService.updateBook(1L, updatedDTO);

		assertThat(result.getBookId()).isEqualTo(1L);
		assertThat(result.getTitle()).isEqualTo("New Title");
		assertThat(result.getAuthor()).isEqualTo("New Author");

		verify(bookRepository).findById(1L);
		verify(bookRepository).save(any(Book.class));
	}

	@Test
	void testUpdateBook_NotFound() {
		when(bookRepository.findById(999L)).thenReturn(Optional.empty());
		BookRequest bookRequest = new BookRequest();
		bookRequest.setTitle("Updated Title");

		assertThrows(BookNotFoundException.class, () -> bookService.updateBook(999L, bookRequest));
		verify(bookRepository).findById(999L);
		verify(bookRepository, never()).save(any());
	}

	@Test
	void testDeleteBook_Found() {
		Book existingBook = new Book();
		existingBook.setBookId(1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));

		bookService.deleteBook(1L);

		verify(bookRepository).findById(1L);
		verify(bookRepository).delete(existingBook);
		verify(reviewRepository).deleteAllByBookId(1L);
		verify(bookLoanRepository).deleteAllByBookId(1L);
	}

	@Test
	void testDeleteBook_NotFound() {
		when(bookRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(999L));

		verify(bookRepository).findById(999L);
		verify(bookRepository, never()).delete(any());
		verify(reviewRepository, never()).deleteAllByBookId(anyLong());
		verify(bookLoanRepository, never()).deleteAllByBookId(anyLong());
	}

	@Test
	void testSearchBooks_MatchingResults() {
		String query = "someTitle";
		Book book1 = new Book();
		book1.setBookId(10L);
		book1.setTitle("SomeTitle here");
		book1.setAuthor("An Author");

		Book book2 = new Book();
		book2.setBookId(20L);
		book2.setTitle("Another Book with SomeTitle inside");
		book2.setAuthor("Another Author");

		when(bookRepository.findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(query, query))
			.thenReturn(List.of(book1, book2));

		List<BookResponse> result = bookService.searchBooks(query);

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getTitle()).containsIgnoringCase("SomeTitle");
		assertThat(result.get(1).getTitle()).containsIgnoringCase("SomeTitle");
		verify(bookRepository).findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(query, query);
	}

	@Test
	void testSearchBooks_NoResults() {
		String query = "no-match";
		when(bookRepository.findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(query, query))
			.thenReturn(List.of());

		List<BookResponse> result = bookService.searchBooks(query);

		assertThat(result).isEmpty();
		verify(bookRepository).findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(query, query);
	}

}
