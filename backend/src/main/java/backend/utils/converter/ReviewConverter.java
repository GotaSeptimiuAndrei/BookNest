package backend.utils.converter;

import backend.dto.request.ReviewRequest;
import backend.dto.response.ReviewResponse;
import backend.model.Review;

import java.time.LocalDate;

public class ReviewConverter {

	public static Review convertToEntity(ReviewRequest request) {
		Review review = new Review();
		review.setUsername(request.getUsername());
		review.setBookId(request.getBookId());
		review.setRating(request.getRating());
		review.setReviewDescription(request.getReviewDescription());
		review.setDate(LocalDate.now());
		return review;
	}

	public static ReviewResponse convertToDto(Review review) {
		ReviewResponse response = new ReviewResponse();
		response.setReviewId(review.getReviewId());
		response.setUsername(review.getUsername());
		response.setBookId(review.getBookId());
		response.setRating(review.getRating());
		response.setReviewDescription(review.getReviewDescription());
		response.setDate(review.getDate());
		return response;
	}

}
