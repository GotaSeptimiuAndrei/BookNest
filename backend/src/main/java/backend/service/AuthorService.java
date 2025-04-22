package backend.service;

import backend.dto.request.AuthorSignupRequest;
import backend.dto.response.AuthorResponse;
import backend.exception.AuthorNotFoundException;
import backend.model.Author;
import backend.model.EmailVerification;
import backend.model.RegistrationType;
import backend.repository.AuthorRepository;
import backend.repository.EmailVerificationRepository;
import backend.utils.VerificationCodeGenerator;
import backend.utils.converter.AuthorConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import static backend.utils.S3Utils.saveFileToS3Bucket;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorService {

	private final AuthorRepository authorRepository;

	private final EmailVerificationRepository emailVerificationRepository;

	private final EmailService emailService;

	private final S3Client s3Client;

	private final String bucketName;

	private final ObjectMapper objectMapper;

	public void registerAuthor(AuthorSignupRequest dto) throws JsonProcessingException {

		if (authorRepository.findByEmail(dto.getEmail()).isPresent())
			throw new IllegalArgumentException("Email already exists.");

		if (emailVerificationRepository.existsByEmailAndVerifiedFalse(dto.getEmail()))
			throw new IllegalArgumentException("A verification code was already sent to this email.");

		String photoUrl = saveFileToS3Bucket(s3Client, bucketName, dto.getPhoto());

		Author authorPayload = AuthorConverter.convertToEntity(dto, photoUrl);
		String payload = objectMapper.writeValueAsString(authorPayload);

		String code = VerificationCodeGenerator.generateVerificationCode();

		EmailVerification v = new EmailVerification();
		v.setEmail(dto.getEmail());
		v.setVerificationCode(code);
		v.setCreatedAt(LocalDateTime.now());
		v.setExpiresAt(LocalDateTime.now().plusMinutes(10));
		v.setRegistrationType(RegistrationType.AUTHOR);
		v.setRegistrationPayload(payload);
		emailVerificationRepository.save(v);

		emailService.sendVerificationEmail(dto.getEmail(), code);
	}

	public Optional<Author> findByEmail(String email) {
		return authorRepository.findByEmail(email);
	}

	public Page<AuthorResponse> getAllAuthorsPaginated(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Author> authorsPage = authorRepository.findAll(pageable);
		return authorsPage.map(AuthorConverter::convertToDto);
	}

	public AuthorResponse getAuthorByFullName(String fullName) {
		Author author = authorRepository.findByFullNameIgnoreCase(fullName)
			.orElseThrow(() -> new AuthorNotFoundException("Author not found with full name: " + fullName));
		return AuthorConverter.convertToDto(author);
	}

	public Page<AuthorResponse> searchAuthorsByName(String query, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Author> authorsPage = authorRepository.findByFullNameIgnoreCaseContaining(query, pageable);
		return authorsPage.map(AuthorConverter::convertToDto);
	}

}
