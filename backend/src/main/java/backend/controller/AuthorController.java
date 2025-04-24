package backend.controller;

import backend.dto.response.APIResponse;
import backend.dto.response.AuthorResponse;
import backend.dto.ErrorDTO;
import backend.exception.AuthorNotFoundException;
import backend.service.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import java.util.List;

import org.springframework.data.domain.Page;

@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Slf4j
public class AuthorController {

	public static final String SUCCESS = "success";

	public static final String ERROR = "error";

	private final AuthorService authorService;

	@GetMapping("/paginated")
	public ResponseEntity<APIResponse<Page<AuthorResponse>>> getAllAuthorsPaginated(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {

		Page<AuthorResponse> authorsPage = authorService.getAllAuthorsPaginated(page, size);
		return ResponseEntity
			.ok(APIResponse.<Page<AuthorResponse>>builder().status(SUCCESS).results(authorsPage).build());
	}

	@GetMapping("/search")
	public ResponseEntity<APIResponse<Page<AuthorResponse>>> searchAuthors(@RequestParam String query,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Page<AuthorResponse> authorsPage = authorService.searchAuthorsByName(query, page, size);
		return ResponseEntity
			.ok(APIResponse.<Page<AuthorResponse>>builder().status(SUCCESS).results(authorsPage).build());
	}

	@GetMapping("/{fullName}")
	public ResponseEntity<APIResponse<AuthorResponse>> getAuthorByFullName(@PathVariable String fullName) {

		AuthorResponse author = authorService.getAuthorByFullName(fullName);
		return ResponseEntity.ok(APIResponse.<AuthorResponse>builder().status(SUCCESS).results(author).build());
	}

	@ExceptionHandler(AuthorNotFoundException.class)
	public ResponseEntity<APIResponse<Void>> handleNotFound(AuthorNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(APIResponse.<Void>builder()
				.status(ERROR)
				.errors(List.of(new ErrorDTO("fullName", ex.getMessage())))
				.build());
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<APIResponse<Void>> handleBindException(BindException ex) {
		List<ErrorDTO> errors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(e -> new ErrorDTO(e.getField(), e.getDefaultMessage()))
			.toList();

		return ResponseEntity.badRequest().body(APIResponse.<Void>builder().status(ERROR).errors(errors).build());
	}

}
