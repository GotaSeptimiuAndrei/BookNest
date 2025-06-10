package backend.controller;

import backend.dto.ErrorDTO;
import backend.dto.response.APIResponse;
import backend.dto.response.CurrentLoansResponse;
import backend.exception.BookLoanException;
import backend.model.BookLoan;
import backend.service.BookLoanService;
import backend.utils.JwtUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = { "http://localhost:3000", "https://booknestlibrary.netlify.app" })
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Slf4j
public class BookLoanController {

	public static final String SUCCESS = "success";

	public static final String ERROR = "error";

	private final BookLoanService bookLoanService;

	@GetMapping
	public ResponseEntity<APIResponse<List<CurrentLoansResponse>>> getCurrentLoansByUser(
			@RequestHeader("Authorization") String token) {

		String username = JwtUtils.extractUsername(token);
		List<CurrentLoansResponse> currentLoans = bookLoanService.currentLoansByUser(username);

		return ResponseEntity
			.ok(APIResponse.<List<CurrentLoansResponse>>builder().status(SUCCESS).results(currentLoans).build());
	}

	@GetMapping("/count")
	public ResponseEntity<APIResponse<Integer>> getCurrentLoansCount(@RequestHeader("Authorization") String token) {

		String username = JwtUtils.extractUsername(token);
		int count = bookLoanService.currentLoansCount(username);

		return ResponseEntity.ok(APIResponse.<Integer>builder().status(SUCCESS).results(count).build());
	}

	@PostMapping("/loan/{bookId}")
	public ResponseEntity<APIResponse<BookLoan>> loanBook(@RequestHeader("Authorization") String token,
			@PathVariable Long bookId) {

		String username = JwtUtils.extractUsername(token);
		BookLoan bookLoan = bookLoanService.loanBook(username, bookId);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(APIResponse.<BookLoan>builder().status(SUCCESS).results(bookLoan).build());
	}

	@DeleteMapping("/return/{bookId}")
	public ResponseEntity<Void> returnBook(@RequestHeader("Authorization") String token, @PathVariable Long bookId) {

		String username = JwtUtils.extractUsername(token);
		bookLoanService.returnBook(username, bookId);

		return ResponseEntity.noContent().build();
	}

	@PutMapping("/renew/{bookId}")
	public ResponseEntity<Void> renewLoan(@RequestHeader("Authorization") String token, @PathVariable Long bookId) {

		String username = JwtUtils.extractUsername(token);
		bookLoanService.renewLoan(username, bookId);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/is-loaned/{bookId}")
	public ResponseEntity<APIResponse<Boolean>> isBookLoanedByUser(@RequestHeader("Authorization") String token,
			@PathVariable Long bookId) {
		String username = JwtUtils.extractUsername(token);
		boolean isLoaned = bookLoanService.isBookLoanedByUser(username, bookId);
		return ResponseEntity.ok(APIResponse.<Boolean>builder().status("success").results(isLoaned).build());
	}

	/**
	 * Handle custom exceptions
	 */
	@ExceptionHandler(BookLoanException.class)
	public ResponseEntity<APIResponse<Void>> handleBookLoanException(BookLoanException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(APIResponse.<Void>builder()
				.status(ERROR)
				.errors(List.of(new ErrorDTO("loan", ex.getMessage())))
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
