package backend.service;

import backend.dto.request.BookRequest;
import backend.dto.response.BookResponse;
import backend.exception.BookNotFoundException;
import backend.model.Book;
import backend.repository.ReviewRepository;
import backend.utils.converter.BookConverter;
import backend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;

	private final ReviewRepository reviewRepository;

	public List<BookResponse> getAllBooks() {
		return bookRepository.findAll().stream().map(BookConverter::convertToDto).toList();
	}

	public BookResponse getBookById(Long id) {
		Book book = bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
		return BookConverter.convertToDto(book);
	}

	public BookResponse saveBook(BookRequest bookRequest) {

		Book book = BookConverter.convertToEntity(bookRequest);

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
		existingBook.setImage(bookRequest.getImage());

		Book updatedBook = bookRepository.save(existingBook);
		return BookConverter.convertToDto(updatedBook);
	}

	public void deleteBook(Long id) {
		Book book = bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
		bookRepository.delete(book);
		reviewRepository.deleteAllByBookId(id);
	}

}
