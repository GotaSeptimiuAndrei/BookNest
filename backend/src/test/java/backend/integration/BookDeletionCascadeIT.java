package backend.integration;

import backend.dto.request.BookRequest;
import backend.dto.request.ReviewRequest;
import backend.dto.response.BookResponse;
import backend.repository.*;
import backend.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.*;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import static org.mockito.ArgumentMatchers.any;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookDeletionCascadeIT {

	@TestConfiguration
	static class BucketStub {

		@Bean("bucketName")
		String bucketName() {
			return "test-bucket";
		}

	}

	@MockitoBean
	S3Client s3Client;

	@Autowired
	BookService bookService;

	@Autowired
	BookLoanService loanService;

	@Autowired
	ReviewService reviewService;

	@Autowired
	BookRepository bookRepo;

	@Autowired
	BookLoanRepository loanRepo;

	@Autowired
	ReviewRepository reviewRepo;

	@BeforeEach
	void seed() {
		when(s3Client.putObject((PutObjectRequest) any(), any(RequestBody.class))).thenReturn(null);
	}

	@Test
	void deleteBook_cascades_to_reviews_and_loans() {

		BookRequest rq = BookRequest.builder()
			.title("Clean Code")
			.author("Robert C. Martin")
			.description("Pragmatic guidelines")
			.copies(1)
			.category("CS")
			.image(new MockMultipartFile("img", "image.jpg", "img/jpg", new byte[] { 1 }))
			.build();
		BookResponse saved = bookService.saveBook(rq);
		Long bookId = saved.getBookId();

		ReviewRequest rr = new ReviewRequest(bookId, 4.5, "Great read!");
		reviewService.createReview("andrei", rr);

		loanService.loanBook("andrei", bookId);

		assertThat(reviewRepo.findAll()).hasSize(1);
		assertThat(loanRepo.findAll()).hasSize(1);

		bookService.deleteBook(bookId);

		assertThat(bookRepo.findById(bookId)).isEmpty();
		assertThat(reviewRepo.findAll()).isEmpty();
		assertThat(loanRepo.findAll()).isEmpty();
	}

}
