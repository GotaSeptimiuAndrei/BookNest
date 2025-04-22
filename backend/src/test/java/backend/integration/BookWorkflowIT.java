package backend.integration;

import backend.dto.request.BookRequest;
import backend.dto.request.UserSignupRequest;
import backend.dto.response.BookResponse;
import backend.model.Book;
import backend.repository.BookRepository;
import backend.repository.BookLoanRepository;
import backend.service.BookLoanService;
import backend.service.BookService;
import backend.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookWorkflowIT {

	@TestConfiguration
	static class BucketStub {

		@Bean("bucketName")
		String bucketName() {
			return "test-bucket";
		}

		@Bean
		JavaMailSender mailSender() {
			return Mockito.mock(JavaMailSender.class);
		}

	}

	@MockitoBean
	S3Client s3Client;

	@Autowired
	BookService bookService;

	@Autowired
	BookLoanService bookLoanService;

	@Autowired
	UserService userService;

	@Autowired
	BookRepository bookRepository;

	@Autowired
	BookLoanRepository bookLoanRepository;

	// @TestConfiguration
	// static class Stubs {
	//
	// /** Replaces the real S3 client */
	// @Bean @Primary
	// S3Client s3Client() {
	// return Mockito.mock(S3Client.class);
	// }
	//
	// /** Supplies the String bean every “bucketName” constructor parameter needs */
	// @Bean("bucketName")
	// String bucketName() {
	// return "test-bucket";
	// }
	//
	// /** (optional) Stub mail sender, websockets, etc. the same way */
	// @Bean
	// JavaMailSender mailSender() {
	// return Mockito.mock(JavaMailSender.class);
	// }
	// }

	@BeforeEach
	void seed() throws JsonProcessingException {
		UserSignupRequest andrei = new UserSignupRequest("andrei@email.com", "password", "andrei");
		userService.registerUser(andrei);

		when(s3Client.putObject((PutObjectRequest) any(), any(RequestBody.class))).thenReturn(null);
	}

	@Test
	void user_can_loan_and_return_a_book() {
		MockMultipartFile dummyImg = new MockMultipartFile("image", "cover.jpg", "image/jpeg", new byte[] { 1, 2 });

		BookRequest req = BookRequest.builder()
			.title("Clean Code")
			.author("Robert C. Martin")
			.description("Pragmatic guidelines")
			.copies(2)
			.category("CS")
			.image(dummyImg)
			.build();
		BookResponse saved = bookService.saveBook(req);
		Long bookId = saved.getBookId();

		bookLoanService.loanBook("andrei", bookId);

		Book afterLoan = bookRepository.findById(bookId).orElseThrow();
		assertThat(afterLoan.getCopiesAvailable()).isEqualTo(1);
		assertThat(bookLoanRepository.findByUsername("andrei")).hasSize(1);

		bookLoanService.returnBook("andrei", bookId);

		Book afterReturn = bookRepository.findById(bookId).orElseThrow();
		assertThat(afterReturn.getCopiesAvailable()).isEqualTo(2);
		assertThat(bookLoanRepository.findByUsername("andrei")).isEmpty();
	}

}
