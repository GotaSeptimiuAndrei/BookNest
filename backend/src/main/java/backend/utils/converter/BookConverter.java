package backend.utils.converter;

import backend.dto.BookDTO;
import backend.model.Book;

public class BookConverter {

	public static Book convertToEntity(BookDTO bookDTO) {
		Book book = new Book();
		book.setTitle(bookDTO.getTitle());
		book.setAuthor(bookDTO.getAuthor());
		book.setDescription(bookDTO.getDescription());
		book.setCopies(bookDTO.getCopies());
		book.setCopiesAvailable(bookDTO.getCopies());
		book.setCategory(bookDTO.getCategory());
		book.setImage(bookDTO.getImage());
		return book;
	}

	public static BookDTO convertToDto(Book book) {
		BookDTO bookDTO = new BookDTO();
		bookDTO.setBookId(book.getBookId());
		bookDTO.setTitle(book.getTitle());
		bookDTO.setAuthor(book.getAuthor());
		bookDTO.setDescription(book.getDescription());
		bookDTO.setCopies(book.getCopies());
		bookDTO.setCopiesAvailable(book.getCopiesAvailable());
		bookDTO.setCategory(book.getCategory());
		bookDTO.setImage(book.getImage());
		return bookDTO;
	}

}
