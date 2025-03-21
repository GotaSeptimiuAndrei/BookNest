package backend.unit.controllerTests;

import backend.controller.ReviewController;
import backend.dto.request.ReviewRequest;
import backend.dto.response.APIResponse;
import backend.dto.response.ReviewResponse;
import backend.exception.ReviewException;
import backend.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewControllerTest {

	@Mock
	private ReviewService reviewService;

	private ReviewController reviewController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		reviewController = new ReviewController(reviewService);
	}

	@Test
	void testCreateReview_Success() {
		ReviewRequest request = new ReviewRequest();
		request.setUsername("testUser");
		request.setBookId(10L);
		request.setRating(4.5);

		ReviewResponse response = new ReviewResponse();
		response.setReviewId(100L);
		response.setUsername("testUser");
		response.setBookId(10L);
		response.setRating(4.5);

		when(reviewService.createReview(any(ReviewRequest.class))).thenReturn(response);

		ResponseEntity<APIResponse<ReviewResponse>> result = reviewController.createReview(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(result.getBody().getStatus()).isEqualTo("success");
		assertThat(result.getBody().getResults().getReviewId()).isEqualTo(100L);

		verify(reviewService).createReview(any(ReviewRequest.class));
	}

	@Test
	void testCreateReview_ThrowsReviewException() {
		ReviewRequest request = new ReviewRequest();
		request.setUsername("user");
		request.setBookId(10L);
		request.setRating(3.0);

		when(reviewService.createReview(any(ReviewRequest.class)))
			.thenThrow(new ReviewException("Review already exists"));

		ResponseEntity<APIResponse<Void>> response = reviewController
			.handleNotFoundException(new ReviewException("Review already exists"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody().getStatus()).isEqualTo("error");
		assertThat(response.getBody().getErrors().get(0).getErrorMessage()).isEqualTo("Review already exists");
	}

	@Test
	void testGetReviewsForBook_Success() {
		Long bookId = 10L;
		ReviewResponse review1 = new ReviewResponse(1L, "Alice", bookId, 4.0, "Good!", LocalDate.now());
		ReviewResponse review2 = new ReviewResponse(2L, "Bob", bookId, 5.0, "Excellent!", LocalDate.now());

		when(reviewService.getAllReviewsForBook(bookId)).thenReturn(List.of(review1, review2));

		ResponseEntity<APIResponse<List<ReviewResponse>>> response = reviewController.getReviewsForBook(bookId);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getStatus()).isEqualTo("success");
		assertThat(response.getBody().getResults()).hasSize(2);
		assertThat(response.getBody().getResults().get(0).getUsername()).isEqualTo("Alice");
		assertThat(response.getBody().getResults().get(1).getUsername()).isEqualTo("Bob");

		verify(reviewService).getAllReviewsForBook(bookId);
	}

}
