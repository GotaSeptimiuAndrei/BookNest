package backend.service;

import backend.dto.request.EmailVerificationRequest;
import backend.dto.request.UserSignupRequest;
import backend.model.Author;
import backend.model.EmailVerification;
import backend.model.RegistrationType;
import backend.model.User;
import backend.repository.AuthorRepository;
import backend.repository.EmailVerificationRepository;
import backend.repository.UserRepository;
import backend.utils.converter.UserConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;

	private final EmailVerificationRepository verificationRepository;

	private final UserRepository userRepository;

	private final AuthorRepository authorRepository;

	private final PasswordEncoder passwordEncoder;

	private final ObjectMapper objectMapper;

	public void sendVerificationEmail(String toEmail, String verificationCode) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("Welcome to Booknest! Here is your email verification code");
		message.setText("Use this code to complete your registration: " + verificationCode);
		mailSender.send(message);
	}

	/**
	 * Verifies a code, then persists the pending User or Author that was serialized in
	 * EmailVerification.registrationPayload
	 */
	@Transactional
	public void verifyAndPersistAccount(EmailVerificationRequest req) {

		EmailVerification v = verificationRepository
			.findByEmailAndVerificationCodeAndVerifiedFalse(req.getEmail(), req.getVerificationCode())
			.orElseThrow(() -> new IllegalArgumentException("Invalid code or already verified"));

		if (v.getExpiresAt().isBefore(LocalDateTime.now()))
			throw new IllegalArgumentException("Verification code expired");

		try {
			if (v.getRegistrationType() == RegistrationType.USER) {
				UserSignupRequest dto = objectMapper.readValue(v.getRegistrationPayload(), UserSignupRequest.class);
				User user = UserConverter.convertToEntity(dto);
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				userRepository.save(user);

			}
			else {
				Author author = objectMapper.readValue(v.getRegistrationPayload(), Author.class);
				author.setPassword(passwordEncoder.encode(author.getPassword()));
				authorRepository.save(author);
			}

			v.setVerified(true);
			verificationRepository.save(v);

		}
		catch (IOException ex) {
			throw new IllegalStateException("Corrupted verification payload", ex);
		}
	}

}
