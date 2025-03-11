package backend.controller;

import backend.dto.request.AuthorSignupRequest;
import backend.dto.request.EmailVerificationRequest;
import backend.dto.request.LoginRequest;
import backend.dto.request.UserSignupRequest;
import backend.model.EmailVerification;
import backend.repository.EmailVerificationRepository;
import backend.service.AuthorService;
import backend.utils.converter.AuthorConverter;
import backend.utils.converter.UserConverter;
import backend.dto.response.TokenResponse;
import backend.service.TokenService;
import backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final TokenService tokenService;

	private final UserService userService;

	private final AuthenticationManager authenticationManager;

	private final AuthorService authorService;

	private final EmailVerificationRepository verificationRepository;

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginRequest loginRequest) {
		try {
			log.info("User {} is trying to login", loginRequest.getEmail());
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
			log.info("User {} has authorities: {}", authentication.getName(), authentication.getAuthorities());
			return ResponseEntity.ok(tokenService.generateToken(authentication));
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/signup-user")
	public ResponseEntity<String> registerUser(@RequestBody UserSignupRequest userSignupRequest) {
		try {
			log.info("User '{}' is trying to register", userSignupRequest.getUsername());
			var user = UserConverter.convertToEntity(userSignupRequest);
			userService.registerUser(user);
			log.info("User '{}' registered successfully", user.getUsername());
			return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/signup-author")
	public ResponseEntity<String> registerUserAuthor(@RequestBody AuthorSignupRequest authorSignupRequest) {
		try {
			log.info("Author '{}' is trying to register", authorSignupRequest.getFullName());
			var author = AuthorConverter.convertToEntity(authorSignupRequest);
			authorService.registerAuthor(author);
			log.info("Author '{}' registered successfully", author.getFullName());
			return ResponseEntity.status(HttpStatus.CREATED).body("Author registered successfully");
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/verify-email")
	public ResponseEntity<String> verifyEmail(@RequestBody EmailVerificationRequest request) {
		var optionalVerification = verificationRepository
			.findByEmailAndVerificationCodeAndVerifiedFalse(request.getEmail(), request.getVerificationCode());

		if (optionalVerification.isEmpty()) {
			return ResponseEntity.badRequest().body("Invalid code or already verified");
		}

		EmailVerification verification = optionalVerification.get();

		if (verification.getExpiresAt() != null && verification.getExpiresAt().isBefore(LocalDateTime.now())) {
			return ResponseEntity.badRequest().body("Verification code expired");
		}

		verification.setVerified(true);
		verificationRepository.save(verification);

		return ResponseEntity.ok("Email verified successfully");
	}

}
