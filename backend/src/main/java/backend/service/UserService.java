package backend.service;

import backend.dto.request.UserSignupRequest;
import backend.model.EmailVerification;
import backend.model.User;
import backend.repository.EmailVerificationRepository;
import backend.repository.UserRepository;
import backend.utils.VerificationCodeGenerator;
import backend.utils.converter.UserConverter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final EmailVerificationRepository emailVerificationRepository;

	private final EmailService emailService;

	public void registerUser(UserSignupRequest userDTO) {
		if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}
		if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}

		User user = UserConverter.convertToEntity(userDTO);
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
