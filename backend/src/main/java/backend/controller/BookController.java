package backend.controller;

import backend.dto.request.BookQuantityUpdateRequest;
import backend.dto.request.BookRequest;
import backend.dto.ErrorDTO;
import backend.dto.response.APIResponse;
import backend.dto.response.BookResponse;
import backend.exception.BookNotFoundException;
import backend.exception.BookValidationException;
import backend.service.BookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin(origins = { "http://localhost:3000", "https://booknestlibrary.netlify.app" })
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

	@GetMapping("/paginated")
	public ResponseEntity<APIResponse<Page<BookResponse>>> getAllBooksPaginated(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Page<BookResponse> booksPage = bookService.getAllBooksPaginated(page, size);

		return ResponseEntity.ok(APIResponse.<Page<BookResponse>>builder().status(SUCCESS).results(booksPage).build());
	}

	@GetMapping("/{id}")
	public ResponseEntity<APIResponse<BookResponse>> getBookById(@PathVariable Long id) {
		BookResponse bookResponse = bookService.getBookById(id);
		return ResponseEntity.ok(APIResponse.<BookResponse>builder().status(SUCCESS).results(bookResponse).build());
	}

	@GetMapping("/search")
	public ResponseEntity<APIResponse<Page<BookResponse>>> searchBooksPaginated(@RequestParam String query,
			@RequestParam(defaultValue = "All") String category, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {

		Page<BookResponse> booksPage = bookService.searchBooks(query, category, page, size);

		return ResponseEntity
			.ok(APIResponse.<Page<BookResponse>>builder().status("success").results(booksPage).build());
	}

	@SecurityRequirement(name = "bearerAuth")
	@PostMapping
	public ResponseEntity<APIResponse<BookResponse>> createBook(@Valid @ModelAttribute BookRequest bookRequest) {
		BookResponse created = bookService.saveBook(bookRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(APIResponse.<BookResponse>builder().status(SUCCESS).results(created).build());
	}

	@SecurityRequirement(name = "bearerAuth")
	@PatchMapping("/{id}/quantity")
	public ResponseEntity<APIResponse<BookResponse>> updateBookQuantity(@PathVariable Long id,
			@Valid @RequestBody BookQuantityUpdateRequest dto) {

		BookResponse updated = bookService.updateBookQuantity(id, dto);

		return ResponseEntity.ok(APIResponse.<BookResponse>builder().status(SUCCESS).results(updated).build());
	}

	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
		bookService.deleteBook(id);
		return ResponseEntity.noContent().build();
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
