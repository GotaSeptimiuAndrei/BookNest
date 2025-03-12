package backend.service;

import backend.model.Author;
import backend.model.EmailVerification;
import backend.repository.AuthorRepository;
import backend.repository.EmailVerificationRepository;
import backend.utils.VerificationCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

	private final AuthorRepository authorRepository;

	private final PasswordEncoder passwordEncoder;

	private final EmailVerificationRepository emailVerificationRepository;

	private final EmailService emailService;

	public void registerAuthor(Author author) {
		if (authorRepository.findByEmail(author.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}

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
