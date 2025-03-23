package backend.service;

import backend.dto.request.ReviewRequest;
import backend.dto.response.ReviewResponse;
import backend.exception.ReviewException;
import backend.model.Review;
import backend.repository.ReviewRepository;
import backend.utils.converter.ReviewConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;

	public ReviewResponse createReview(String username, ReviewRequest reviewRequest) {
		Review review = ReviewConverter.convertToEntity(reviewRequest, username);

		if (reviewRepository.findByUsernameAndBookId(username, reviewRequest.getBookId()).isPresent()) {
			throw new ReviewException("Review already exists");
		}

		reviewRepository.save(review);

		return ReviewConverter.convertToDto(review);
	}

	public List<ReviewResponse> getAllReviewsForBook(Long bookId) {
		List<Review> reviews = reviewRepository.findAll().stream().filter(r -> r.getBookId().equals(bookId)).toList();

		return reviews.stream().map(ReviewConverter::convertToDto).toList();
	}

}
