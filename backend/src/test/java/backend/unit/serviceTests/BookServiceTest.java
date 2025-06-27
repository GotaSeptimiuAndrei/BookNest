package backend.unit.serviceTests;

import backend.dto.request.BookQuantityUpdateRequest;
import backend.dto.request.BookRequest;
import backend.dto.response.BookResponse;
import backend.exception.BookNotFoundException;
import backend.exception.BookValidationException;
import backend.model.Book;
import backend.repository.BookLoanRepository;
import backend.repository.BookRepository;
import backend.repository.ReviewRepository;
import backend.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

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

	@Mock
	private S3Client s3Client;

	private BookService bookService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		String bucketName = "bucket-booknest";
		bookService = new BookService(bookRepository, reviewRepository, bookLoanRepository, s3Client, bucketName);
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
		MockMultipartFile mockFile = new MockMultipartFile("image", "test-image.jpg", "image/jpeg",
				"DummyImageContent".getBytes());

		BookRequest bookRequest = new BookRequest();
		bookRequest.setTitle("New Book");
		bookRequest.setAuthor("John Doe");
		bookRequest.setDescription("Just a test");
		bookRequest.setCopies(10);
		bookRequest.setCopiesAvailable(10);
		bookRequest.setCategory("Fiction");
		bookRequest.setImage(mockFile);

		Book convertedEntity = new Book();
		convertedEntity.setBookId(5L);
		convertedEntity.setTitle("New Book");
		convertedEntity.setAuthor("John Doe");
		convertedEntity.setDescription("Just a test");
		convertedEntity.setCopies(10);
		convertedEntity.setCopiesAvailable(10);
		convertedEntity.setCategory("Fiction");
		convertedEntity.setImage("https://bucketName.s3.amazonaws.com/test-image.jpg");

		when(bookRepository.save(any(Book.class))).thenReturn(convertedEntity);

		BookResponse result = bookService.saveBook(bookRequest);

		assertThat(result).isNotNull();
		assertThat(result.getBookId()).isEqualTo(5L);
		assertThat(result.getTitle()).isEqualTo("New Book");
		assertThat(result.getAuthor()).isEqualTo("John Doe");
		assertThat(result.getImage()).isEqualTo("https://bucketName.s3.amazonaws.com/test-image.jpg");

		ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
		verify(bookRepository).save(bookCaptor.capture());
		Book savedBook = bookCaptor.getValue();

		assertThat(savedBook.getTitle()).isEqualTo("New Book");
		assertThat(savedBook.getImage()).isNotEmpty();
	}

	@Test
	void updateBookQuantity_increase_success() {
		Book book = new Book();
		book.setBookId(1L);
		book.setTitle("New Book");
		book.setAuthor("New Author");
		book.setCopies(3);
		book.setCopiesAvailable(3);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookRepository.save(book)).thenAnswer(inv -> inv.getArgument(0));

		BookQuantityUpdateRequest dto = new BookQuantityUpdateRequest(1);

		BookResponse resp = bookService.updateBookQuantity(1L, dto);

		assertThat(resp.getBookId()).isEqualTo(1L);
		assertThat(resp.getCopies()).isEqualTo(4);
		assertThat(resp.getCopiesAvailable()).isEqualTo(4);

		verify(bookRepository).findById(1L);
		verify(bookRepository).save(book);
	}

	@Test
	void updateBookQuantity_decrease_belowZero_throws() {
		Book book = new Book();
		book.setBookId(1L);
		book.setTitle("New Book");
		book.setAuthor("New Author");
		book.setCopies(0);
		book.setCopiesAvailable(0);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		BookQuantityUpdateRequest dto = new BookQuantityUpdateRequest(-1);

		assertThrows(BookValidationException.class, () -> bookService.updateBookQuantity(1L, dto));

		verify(bookRepository).findById(1L);
		verify(bookRepository, never()).save(any());
	}

	@Test
	void updateBookQuantity_notFound_throws() {
		when(bookRepository.findById(99L)).thenReturn(Optional.empty());

		BookQuantityUpdateRequest dto = new BookQuantityUpdateRequest(1);

		assertThrows(BookNotFoundException.class, () -> bookService.updateBookQuantity(99L, dto));

		verify(bookRepository).findById(99L);
		verify(bookRepository, never()).save(any());
	}

	@Test
	void testDeleteBook_Found() {
		Book existingBook = new Book();
		existingBook.setBookId(1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));

		bookService.deleteBook(1L);

		verify(bookRepository).findById(1L);
		verify(reviewRepository).deleteAllByBookId(1L);
		verify(bookLoanRepository).deleteAllByBookId(1L);
		verify(bookRepository).delete(existingBook);
	}

	@Test
	void testDeleteBook_NotFound() {
		when(bookRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(999L));

		verify(bookRepository).findById(999L);
		verify(reviewRepository, never()).deleteAllByBookId(anyLong());
		verify(bookLoanRepository, never()).deleteAllByBookId(anyLong());
		verify(bookRepository, never()).delete(any());
	}

	@Test
	void searchBooks_allCategory_matchingResults() {
		int page = 0, size = 10;
		String query = "someTitle";
		String category = "All";

		Book b1 = new Book();
		b1.setTitle("SomeTitle here");
		Book b2 = new Book();
		b2.setTitle("Another SomeTitle");

		Page<Book> bookPage = new PageImpl<>(List.of(b1, b2), PageRequest.of(page, size), 2);

		when(bookRepository.findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(query, query,
				PageRequest.of(page, size)))
			.thenReturn(bookPage);

		Page<BookResponse> result = bookService.searchBooks(query, category, page, size);

		assertThat(result).hasSize(2);
		verify(bookRepository).findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(query, query,
				PageRequest.of(page, size));
	}

	@Test
	void searchBooks_specificCategory_callsCategoryRepo() {
		int page = 0, size = 10;
		String query = "harry";
		String category = "Science‑Fiction";

		Page<Book> empty = Page.empty(PageRequest.of(page, size));

		when(bookRepository
			.findByCategoryIgnoreCaseAndTitleIgnoreCaseContainingOrCategoryIgnoreCaseAndAuthorIgnoreCaseContaining(
					category, query, category, query, PageRequest.of(page, size)))
			.thenReturn(empty);

		Page<BookResponse> result = bookService.searchBooks(query, category, page, size);

		assertThat(result).isEmpty();
		verify(bookRepository)
			.findByCategoryIgnoreCaseAndTitleIgnoreCaseContainingOrCategoryIgnoreCaseAndAuthorIgnoreCaseContaining(
					category, query, category, query, PageRequest.of(page, size));
	}

}
