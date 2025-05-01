package backend.controller;

import backend.dto.ErrorDTO;
import backend.dto.request.ReviewRequest;
import backend.dto.response.APIResponse;
import backend.dto.response.ReviewResponse;
import backend.exception.ReviewException;
import backend.service.ReviewService;
import backend.utils.JwtUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

	private final ReviewService reviewService;

	public static final String SUCCESS = "success";

	public static final String ERROR = "error";

	@SecurityRequirement(name = "bearerAuth")
	@PostMapping
	public ResponseEntity<APIResponse<ReviewResponse>> createReview(@RequestHeader("Authorization") String token,
			@Valid @RequestBody ReviewRequest reviewRequest) {

		String username = JwtUtils.extractUsername(token);

		ReviewResponse created = reviewService.createReview(username, reviewRequest);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(APIResponse.<ReviewResponse>builder().status(SUCCESS).results(created).build());
	}

	@GetMapping("/book/{bookId}")
	public ResponseEntity<APIResponse<List<ReviewResponse>>> getReviewsForBook(@PathVariable Long bookId) {
		List<ReviewResponse> reviews = reviewService.getAllReviewsForBook(bookId);
		return ResponseEntity.ok(APIResponse.<List<ReviewResponse>>builder().status(SUCCESS).results(reviews).build());
	}

	@GetMapping("/book/{bookId}/paginated")
	public ResponseEntity<APIResponse<Page<ReviewResponse>>> getReviewsForBookPaginated(@PathVariable Long bookId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Page<ReviewResponse> reviewPage = reviewService.getAllReviewsForBookPaginated(bookId, page, size);
		return ResponseEntity
			.ok(APIResponse.<Page<ReviewResponse>>builder().status(SUCCESS).results(reviewPage).build());
	}

	@ExceptionHandler(ReviewException.class)
	public ResponseEntity<APIResponse<Void>> handleNotFoundException(ReviewException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(APIResponse.<Void>builder()
				.status(ERROR)
				.errors(List.of(new ErrorDTO("review", ex.getMessage())))
				.build());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<APIResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		List<ErrorDTO> errorList = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(fieldError -> new ErrorDTO(fieldError.getField(), fieldError.getDefaultMessage()))
			.toList();

		return ResponseEntity.badRequest().body(APIResponse.<Void>builder().status(ERROR).errors(errorList).build());
	}

}
