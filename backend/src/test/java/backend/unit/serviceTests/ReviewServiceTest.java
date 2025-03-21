package backend.unit.serviceTests;

import backend.dto.request.ReviewRequest;
import backend.dto.response.ReviewResponse;
import backend.exception.ReviewException;
import backend.model.Review;
import backend.repository.ReviewRepository;
import backend.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	private ReviewService reviewService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		reviewService = new ReviewService(reviewRepository);
	}

	@Test
	void testCreateReview_Success() {
		ReviewRequest request = new ReviewRequest();
		request.setUsername("testUser");
		request.setBookId(10L);
		request.setRating(4.5);
		request.setReviewDescription("Excellent book!");

		when(reviewRepository.findByUsernameAndBookId("testUser", 10L)).thenReturn(Optional.empty());

		Review savedReview = new Review();
		savedReview.setReviewId(100L);
		savedReview.setUsername("testUser");
		savedReview.setBookId(10L);
		savedReview.setRating(4.5);
		savedReview.setReviewDescription("Excellent book!");
		savedReview.setDate(LocalDate.now());

		when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

		ReviewResponse result = reviewService.createReview(request);

		assertThat(result).isNotNull();
		assertThat(result.getReviewId()).isEqualTo(100L);
		assertThat(result.getUsername()).isEqualTo("testUser");
		assertThat(result.getBookId()).isEqualTo(10L);
		assertThat(result.getRating()).isEqualTo(4.5);
		assertThat(result.getReviewDescription()).isEqualTo("Excellent book!");

		verify(reviewRepository).save(any(Review.class));
	}

	@Test
	void testCreateReview_AlreadyExists_ThrowsReviewException() {
		ReviewRequest request = new ReviewRequest();
		request.setUsername("testUser");
		request.setBookId(10L);
		request.setRating(3.0);

		when(reviewRepository.findByUsernameAndBookId("testUser", 10L)).thenReturn(Optional.of(new Review()));

		assertThrows(ReviewException.class, () -> reviewService.createReview(request));
		verify(reviewRepository, never()).save(any());
	}

	@Test
	void testGetAllReviewsForBook_ReturnsList() {
		Long bookId = 10L;

		Review review1 = new Review();
		review1.setReviewId(1L);
		review1.setUsername("Alice");
		review1.setBookId(bookId);
		review1.setRating(4.0);

		Review review2 = new Review();
		review2.setReviewId(2L);
		review2.setUsername("Bob");
		review2.setBookId(bookId);
		review2.setRating(5.0);

		when(reviewRepository.findAll()).thenReturn(List.of(review1, review2));

		List<ReviewResponse> result = reviewService.getAllReviewsForBook(bookId);

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getReviewId()).isEqualTo(1L);
		assertThat(result.get(0).getUsername()).isEqualTo("Alice");
		assertThat(result.get(1).getUsername()).isEqualTo("Bob");

		verify(reviewRepository).findAll();
	}

}
