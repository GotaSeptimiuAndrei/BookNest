package backend.service;

import backend.dto.request.BookRequest;
import backend.dto.response.BookResponse;
import backend.exception.BookNotFoundException;
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

	public Page<BookResponse> searchBooksByTitleOrAuthor(String query, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Book> booksPage = bookRepository.findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(query, query,
				pageable);

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

	public BookResponse updateBook(Long id, BookRequest bookRequest) {

		Book existingBook = bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

		existingBook.setTitle(bookRequest.getTitle());
		existingBook.setAuthor(bookRequest.getAuthor());
		existingBook.setDescription(bookRequest.getDescription());
		existingBook.setCopies(bookRequest.getCopies());
		existingBook.setCategory(bookRequest.getCategory());
		if (bookRequest.getImage() != null) {
			existingBook.setImage(saveFileToS3Bucket(s3Client, bucketName, bookRequest.getImage()));
		}
		else {
			existingBook.setImage("image.jpg");
		}

		Book updatedBook = bookRepository.save(existingBook);
		return BookConverter.convertToDto(updatedBook);
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
