package backend.unit.controllerTests;

import backend.controller.ReviewController;
import backend.dto.request.ReviewRequest;
import backend.dto.response.ReviewResponse;
import backend.exception.ReviewException;
import backend.service.ReviewService;
import backend.utils.JwtUtils;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReviewService reviewService;

	private MockedStatic<JwtUtils> jwtUtilsMock;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		jwtUtilsMock = Mockito.mockStatic(JwtUtils.class);
		jwtUtilsMock.when(() -> JwtUtils.extractUsername(Mockito.anyString())).thenReturn("testUser");
	}

	@AfterEach
	void tearDown() {
		jwtUtilsMock.close();
	}

	@Test
	void testCreateReview_Success() throws Exception {
		ReviewRequest request = new ReviewRequest();
		request.setBookId(10L);
		request.setRating(4.5);

		ReviewResponse mockResponse = new ReviewResponse();
		mockResponse.setReviewId(100L);
		mockResponse.setUsername("testUser");
		mockResponse.setBookId(10L);
		mockResponse.setRating(4.5);

		when(reviewService.createReview(eq("testUser"), any(ReviewRequest.class))).thenReturn(mockResponse);

		mockMvc
			.perform(post("/api/reviews").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer token")
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.reviewId").value(100))
			.andExpect(jsonPath("$.results.username").value("testUser"));
	}

	@Test
	void testCreateReview_ThrowsReviewException() throws Exception {
		ReviewRequest request = new ReviewRequest();
		request.setBookId(10L);
		request.setRating(3.0);

		when(reviewService.createReview(eq("testUser"), any(ReviewRequest.class)))
			.thenThrow(new ReviewException("Review already exists"));

		// The controller's @ExceptionHandler maps ReviewException to status 404
		mockMvc
			.perform(post("/api/reviews").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer token")
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("Review already exists"));
	}

	@Test
	void testGetReviewsForBook_Success() throws Exception {
		Long bookId = 10L;

		ReviewResponse review1 = new ReviewResponse(1L, "Alice", bookId, 4.0, "Great book!", LocalDate.now());
		ReviewResponse review2 = new ReviewResponse(2L, "Bob", bookId, 5.0, "Amazing read!", LocalDate.now());

		when(reviewService.getAllReviewsForBook(bookId)).thenReturn(List.of(review1, review2));

		mockMvc.perform(get("/api/reviews/book/{bookId}", bookId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.length()").value(2))
			.andExpect(jsonPath("$.results[0].reviewId").value(1))
			.andExpect(jsonPath("$.results[0].username").value("Alice"))
			.andExpect(jsonPath("$.results[1].reviewId").value(2))
			.andExpect(jsonPath("$.results[1].username").value("Bob"));
	}

}