package backend.controller;

import backend.dto.request.BookRequest;
import backend.dto.ErrorDTO;
import backend.dto.response.APIResponse;
import backend.dto.response.BookResponse;
import backend.exception.BookNotFoundException;
import backend.exception.BookValidationException;
import backend.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

	public static final String SUCCESS = "success";

	public static final String ERROR = "error";

	private final BookService bookService;

	@GetMapping
	public ResponseEntity<APIResponse<List<BookResponse>>> getAllBooks() {
		List<BookResponse> books = bookService.getAllBooks();
		return ResponseEntity.ok(APIResponse.<List<BookResponse>>builder().status(SUCCESS).results(books).build());
	}

	@GetMapping("/{id}")
	public ResponseEntity<APIResponse<BookResponse>> getBookById(@PathVariable Long id) {
		BookResponse bookResponse = bookService.getBookById(id);
		return ResponseEntity.ok(APIResponse.<BookResponse>builder().status(SUCCESS).results(bookResponse).build());
	}

	@GetMapping("/search")
	public ResponseEntity<APIResponse<List<BookResponse>>> searchBooks(@RequestParam("query") String query) {
		List<BookResponse> matchingBooks = bookService.searchBooks(query);

		return ResponseEntity
			.ok(APIResponse.<List<BookResponse>>builder().status("success").results(matchingBooks).build());
	}

	@PostMapping
	public ResponseEntity<APIResponse<BookResponse>> createBook(@Valid @RequestBody BookRequest bookRequest) {
		BookResponse created = bookService.saveBook(bookRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(APIResponse.<BookResponse>builder().status(SUCCESS).results(created).build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<APIResponse<BookResponse>> updateBook(@PathVariable Long id,
			@Valid @RequestBody BookRequest bookRequest) {
		BookResponse updated = bookService.updateBook(id, bookRequest);
		return ResponseEntity.ok(APIResponse.<BookResponse>builder().status(SUCCESS).results(updated).build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<APIResponse<Void>> deleteBook(@PathVariable Long id) {
		bookService.deleteBook(id);
		return ResponseEntity.ok(APIResponse.<Void>builder().status(SUCCESS).build());
	}

	/**
	 * Handle custom exceptions
	 */
	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<APIResponse<Void>> handleNotFoundException(BookNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(APIResponse.<Void>builder()
				.status(ERROR)
				.errors(List.of(new ErrorDTO("id", ex.getMessage())))
				.build());
	}

	@ExceptionHandler(BookValidationException.class)
	public ResponseEntity<APIResponse<Void>> handleValidationException(BookValidationException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(APIResponse.<Void>builder()
				.status(ERROR)
				.errors(List.of(new ErrorDTO("validation", ex.getMessage())))
				.build());
	}

	/**
	 * Handle bean validation errors
	 */
	@ExceptionHandler(BindException.class)
	public ResponseEntity<APIResponse<Void>> handleBindException(BindException ex) {
		List<ErrorDTO> errorList = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(fieldError -> new ErrorDTO(fieldError.getField(), fieldError.getDefaultMessage()))
			.toList();

		return ResponseEntity.badRequest().body(APIResponse.<Void>builder().status(ERROR).errors(errorList).build());
	}

}
