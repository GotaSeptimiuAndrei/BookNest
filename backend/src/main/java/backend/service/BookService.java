package backend.service;

import backend.dto.request.BookRequest;
import backend.dto.response.BookResponse;
import backend.exception.BookNotFoundException;
import backend.model.Book;
import backend.repository.BookLoanRepository;
import backend.repository.ReviewRepository;
import backend.utils.converter.BookConverter;
import backend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;

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

	public BookResponse getBookById(Long id) {
		Book book = bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
		return BookConverter.convertToDto(book);
	}

	public BookResponse saveBook(BookRequest bookRequest) {
		String imageUrl = saveFileToS3Bucket(bookRequest.getImage());

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
			existingBook.setImage(saveFileToS3Bucket(bookRequest.getImage()));
		}
		else {
			existingBook.setImage("image.jpg");
		}

		Book updatedBook = bookRepository.save(existingBook);
		return BookConverter.convertToDto(updatedBook);
	}

	public void deleteBook(Long id) {
		Book book = bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
		bookRepository.delete(book);
		reviewRepository.deleteAllByBookId(id);
		bookLoanRepository.deleteAllByBookId(id);
	}

	public List<BookResponse> searchBooks(String query) {
		List<Book> matchingBooks = bookRepository.findByTitleIgnoreCaseContainingOrAuthorIgnoreCaseContaining(query,
				query);

		return matchingBooks.stream().map(BookConverter::convertToDto).toList();
	}

	private String saveFileToS3Bucket(MultipartFile file) {
		try {
			String fileName = file.getOriginalFilename();
			String contentType = file.getContentType();
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.contentType(contentType)
				.key(fileName)
				.build();

			RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

			s3Client.putObject(putObjectRequest, requestBody);

			return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not upload the file to S3: " + e.getMessage());
		}
	}

}
