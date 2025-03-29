package backend.service;

import backend.dto.request.AuthorSignupRequest;
import backend.model.Author;
import backend.model.EmailVerification;
import backend.repository.AuthorRepository;
import backend.repository.EmailVerificationRepository;
import backend.utils.VerificationCodeGenerator;
import backend.utils.converter.AuthorConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import static backend.utils.S3Utils.saveFileToS3Bucket;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

	private final AuthorRepository authorRepository;

	private final PasswordEncoder passwordEncoder;

	private final EmailVerificationRepository emailVerificationRepository;

	private final EmailService emailService;

	private final S3Client s3Client;

	private final String bucketName;

	public void registerAuthor(AuthorSignupRequest authorDTO) {
		if (authorRepository.findByEmail(authorDTO.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}

		String photoUrl = saveFileToS3Bucket(s3Client, bucketName, authorDTO.getPhoto());
		Author author = AuthorConverter.convertToEntity(authorDTO, photoUrl);

		author.setPassword(passwordEncoder.encode(author.getPassword()));

		authorRepository.save(author);

		String code = VerificationCodeGenerator.generateVerificationCode();
		EmailVerification verification = new EmailVerification();
		verification.setEmail(author.getEmail());
		verification.setVerificationCode(code);
		verification.setCreatedAt(LocalDateTime.now());
		verification.setExpiresAt(LocalDateTime.now().plusMinutes(10));

		emailVerificationRepository.save(verification);

		emailService.sendVerificationEmail(author.getEmail(), code);
	}

	public Optional<Author> findByEmail(String email) {
		return authorRepository.findByEmail(email);
	}

}
