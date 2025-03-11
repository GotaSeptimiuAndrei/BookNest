package backend.service;

import backend.model.EmailVerification;
import backend.model.User;
import backend.repository.EmailVerificationRepository;
import backend.repository.UserRepository;
import backend.utils.VerificationCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final EmailVerificationRepository emailVerificationRepository;
	private final EmailService emailService;

	public void registerUser(User user) {
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		userRepository.save(user);

		String code = VerificationCodeGenerator.generateVerificationCode();
		EmailVerification verification = new EmailVerification();
		verification.setEmail(user.getEmail());
		verification.setVerificationCode(code);
		verification.setCreatedAt(LocalDateTime.now());
		verification.setExpiresAt(LocalDateTime.now().plusMinutes(10));

		emailVerificationRepository.save(verification);

		emailService.sendVerificationEmail(user.getEmail(), code);
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

}
