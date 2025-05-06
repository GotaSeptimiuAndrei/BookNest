package backend.service;

import backend.dto.request.BookQuantityUpdateRequest;
import backend.dto.request.BookRequest;
import backend.dto.response.BookResponse;
import backend.exception.BookNotFoundException;
import backend.exception.BookValidationException;
import backend.model.Book;
import backend.repository.BookLoanRepository;
import backend.repository.ReviewRepository;
import backend.utils.converter.BookConverter;
import backend.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

import static backend.utils.S3Utils.saveFileToS3Bucket;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;

	private final ReviewRepository reviewRepository;

	private final BookLoanRepository bookLoanRepository;

	private final S3Client s3Client;

	private final String bucketName;

	public List<BookResponse> getAllBooks() {
		return bookRepository.findAll().stream().map(BookConverter::convertToDto).toList();
	}

	public Page<BookResponse> getAllBooksPaginated(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Book> booksPage = bookRepository.findAll(pageable);

		return booksPage.map(BookConverter::convertToDto);
	}

	public Page<BookResponse> searchBooks(String query, String category, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		Page<Book> booksPage = "All".equalsIgnoreCase(category)
				? bookRepository.findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(query, query, pageable)
				: bookRepository
					.findByCategoryIgnoreCaseAndTitleIgnoreCaseContainingOrCategoryIgnoreCaseAndAuthorIgnoreCaseContaining(
							category, query, category, query, pageable);

		return booksPage.map(BookConverter::convertToDto);
	}

	public BookResponse getBookById(Long id) {
		Book book = bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
		return BookConverter.convertToDto(book);
	}

	public BookResponse saveBook(BookRequest bookRequest) {
		String imageUrl = saveFileToS3Bucket(s3Client, bucketName, bookRequest.getImage());

		Book book = BookConverter.convertToEntity(bookRequest, imageUrl);
		Book savedBook = bookRepository.save(book);
		return BookConverter.convertToDto(savedBook);
	}

	public BookResponse updateBookQuantity(Long id, BookQuantityUpdateRequest dto) {

		Book book = bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

		int delta = dto.getDelta();

		int newCopies = book.getCopies() + delta;
		int newCopiesAvailable = book.getCopiesAvailable() + delta;

		if (newCopies < 0 || newCopiesAvailable < 0) {
			throw new BookValidationException("Cannot reduce quantity below zero.");
		}

		if (newCopiesAvailable > newCopies) {
			throw new BookValidationException("Available copies cannot exceed total copies.");
		}

		book.setCopies(newCopies);
		book.setCopiesAvailable(newCopiesAvailable);

		Book saved = bookRepository.save(book);
		return BookConverter.convertToDto(saved);
	}

	@Transactional
	public void deleteBook(Long id) {
		Book book = bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
		bookRepository.delete(book);
		reviewRepository.deleteAllByBookId(id);
		bookLoanRepository.deleteAllByBookId(id);
	}

}
