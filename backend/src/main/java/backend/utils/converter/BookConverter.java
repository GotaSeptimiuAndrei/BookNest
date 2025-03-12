package backend.utils.converter;

import backend.dto.request.BookRequest;
import backend.dto.response.BookResponse;
import backend.model.Book;

public class BookConverter {

	public static Book convertToEntity(BookRequest bookRequest) {
		Book book = new Book();
		book.setTitle(bookRequest.getTitle());
		book.setAuthor(bookRequest.getAuthor());
		book.setDescription(bookRequest.getDescription());
		book.setCopies(bookRequest.getCopies());
		book.setCopiesAvailable(bookRequest.getCopies());
		book.setCategory(bookRequest.getCategory());
		book.setImage(bookRequest.getImage());
		return book;
	}

	public static BookResponse convertToDto(Book book) {
		BookResponse bookResponse = new BookResponse();
		bookResponse.setBookId(book.getBookId());
		bookResponse.setTitle(book.getTitle());
		bookResponse.setAuthor(book.getAuthor());
		bookResponse.setDescription(book.getDescription());
		bookResponse.setCopies(book.getCopies());
		bookResponse.setCopiesAvailable(book.getCopiesAvailable());
		bookResponse.setCategory(book.getCategory());
		bookResponse.setImage(book.getImage());
		return bookResponse;
	}

}
