package backend.service;

import backend.dto.BookDTO;
import backend.exception.BookNotFoundException;
import backend.model.Book;
import backend.utils.converter.BookConverter;
import backend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookConverter::convertToDto)
                .toList();
    }

    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        return BookConverter.convertToDto(book);
    }

    public BookDTO saveBook(BookDTO bookDTO) {

        Book book = BookConverter.convertToEntity(bookDTO);

        Book savedBook = bookRepository.save(book);
        return BookConverter.convertToDto(savedBook);
    }

    public BookDTO updateBook(Long id, BookDTO bookDTO) {

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setDescription(bookDTO.getDescription());
        existingBook.setCopies(bookDTO.getCopies());
        existingBook.setCategory(bookDTO.getCategory());
        existingBook.setImage(bookDTO.getImage());

        Book updatedBook = bookRepository.save(existingBook);
        return BookConverter.convertToDto(updatedBook);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        bookRepository.delete(book);
    }

}
