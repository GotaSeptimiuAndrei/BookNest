package backend.unit.serviceTests;

import backend.dto.request.AuthorSignupRequest;
import backend.model.*;
import backend.repository.AuthorRepository;
import backend.repository.EmailVerificationRepository;
import backend.service.AuthorService;
import backend.service.EmailService;
import backend.utils.VerificationCodeGenerator;
import backend.utils.converter.AuthorConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.LocalDate;
import java.util.Optional;

import static backend.utils.S3Utils.saveFileToS3Bucket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

	@Mock
	AuthorRepository authorRepository;

	@Mock
	EmailVerificationRepository emailVerificationRepository;

	@Mock
	EmailService emailService;

	@Mock
	S3Client s3Client;

	ObjectMapper objectMapper = new ObjectMapper();

	AuthorService authorService;

	@BeforeEach
	void init() {
		authorService = new AuthorService(authorRepository, emailVerificationRepository, emailService, s3Client,
				"bucket", objectMapper);
	}

	@Test
	void registerAuthor_success() throws Exception {
		MockMultipartFile mockFile = new MockMultipartFile("image", "test-image.jpg", "image/jpeg",
				"DummyImageContent".getBytes());
		AuthorSignupRequest req = new AuthorSignupRequest("Jane Doe", "jane@example.com", "pwd",
				LocalDate.of(1990, 1, 1), "City", "Country", "My bio", mockFile);

		when(authorRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
		when(emailVerificationRepository.existsByEmailAndVerifiedFalse("jane@example.com")).thenReturn(false);

		try (MockedStatic<VerificationCodeGenerator> codeMock = mockStatic(VerificationCodeGenerator.class);
				MockedStatic<AuthorConverter> convMock = mockStatic(AuthorConverter.class);
				MockedStatic<backend.utils.S3Utils> s3Mock = mockStatic(backend.utils.S3Utils.class)) {

			codeMock.when(VerificationCodeGenerator::generateVerificationCode).thenReturn("111111");
			s3Mock.when(() -> saveFileToS3Bucket(s3Client, "bucket", mockFile)).thenReturn("url");
			Author payload = new Author();
			convMock.when(() -> AuthorConverter.convertToEntity(req, "url")).thenReturn(payload);

			authorService.registerAuthor(req);

			verify(authorRepository, never()).save(any());
			verify(emailVerificationRepository).save(any(EmailVerification.class));
			verify(emailService).sendVerificationEmail("jane@example.com", "111111");
		}
	}

	@Test
	void registerAuthor_emailExists() {
		AuthorSignupRequest req = new AuthorSignupRequest();
		req.setEmail("dup@example.com");
		when(authorRepository.findByEmail("dup@example.com")).thenReturn(Optional.of(new Author()));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> authorService.registerAuthor(req));

		assertThat(ex.getMessage()).isEqualTo("Email already exists.");
		verify(emailVerificationRepository, never()).save(any());
	}

	@Test
	void registerAuthor_emailPending() {
		AuthorSignupRequest req = new AuthorSignupRequest();
		req.setEmail("wait@example.com");
		when(authorRepository.findByEmail("wait@example.com")).thenReturn(Optional.empty());
		when(emailVerificationRepository.existsByEmailAndVerifiedFalse("wait@example.com")).thenReturn(true);

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> authorService.registerAuthor(req));

		assertThat(ex.getMessage()).isEqualTo("A verification code was already sent to this email.");
		verify(emailVerificationRepository, never()).save(any());
	}

}
