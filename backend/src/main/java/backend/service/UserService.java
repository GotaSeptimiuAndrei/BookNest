package backend.service;

import backend.dto.request.UserSignupRequest;
import backend.model.EmailVerification;
import backend.model.RegistrationType;
import backend.model.User;
import backend.repository.EmailVerificationRepository;
import backend.repository.UserRepository;
import backend.utils.VerificationCodeGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	public void registerUser(UserSignupRequest dto) throws JsonProcessingException {
		if (userRepository.findByEmail(dto.getEmail()).isPresent())
			throw new IllegalArgumentException("Email already exists");

		if (emailVerificationRepository.existsByEmailAndVerifiedFalse(dto.getEmail()))
			throw new IllegalArgumentException("A verification code was already sent to this email");

		String code = VerificationCodeGenerator.generateVerificationCode();

		EmailVerification v = new EmailVerification();
		v.setEmail(dto.getEmail());
		v.setVerificationCode(code);
		v.setCreatedAt(LocalDateTime.now());
		v.setExpiresAt(LocalDateTime.now().plusMinutes(10));
		v.setRegistrationType(RegistrationType.USER);
		v.setRegistrationPayload(new ObjectMapper().writeValueAsString(dto));
		emailVerificationRepository.save(v);

		emailService.sendVerificationEmail(dto.getEmail(), code);
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

}
