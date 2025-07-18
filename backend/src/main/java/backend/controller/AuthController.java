package backend.controller;

import backend.dto.AdminDTO;
import backend.dto.request.AuthorSignupRequest;
import backend.dto.request.EmailVerificationRequest;
import backend.dto.request.LoginRequest;
import backend.dto.request.UserSignupRequest;
import backend.service.AuthorService;
import backend.service.EmailService;
import backend.service.TokenService;
import backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = { "http://localhost:3000", "https://booknestlibrary.netlify.app" })
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final TokenService tokenService;

	private final UserService userService;

	private final AuthenticationManager authenticationManager;

	private final AuthorService authorService;

	private final EmailService emailService;

	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {
		try {
			log.info("User {} is trying to login", loginRequest.getEmail());
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

			log.info("User {} has authorities: {}", authentication.getName(), authentication.getAuthorities());
			return ResponseEntity.ok(tokenService.generateToken(authentication));

		}
		catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
		}
		catch (Exception ex) {
			log.error("Unexpected error during login", ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
		}
	}

	@PostMapping("/signup-user")
	public ResponseEntity<String> registerUser(@Valid @RequestBody UserSignupRequest userSignupRequest) {
		try {
			log.info("User '{}' is trying to register", userSignupRequest.getUsername());
			userService.registerUser(userSignupRequest);
			log.info("User '{}' registered successfully", userSignupRequest.getUsername());
			return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/signup-author")
	public ResponseEntity<String> registerUserAuthor(@Valid @ModelAttribute AuthorSignupRequest authorSignupRequest) {
		try {
			log.info("Author '{}' is trying to register", authorSignupRequest.getFullName());
			authorService.registerAuthor(authorSignupRequest);
			log.info("Author '{}' registered successfully", authorSignupRequest.getFullName());
			return ResponseEntity.status(HttpStatus.CREATED).body("Author registered successfully");
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/create-admin")
	public ResponseEntity<String> createAdmin(@Valid @RequestBody AdminDTO request) {
		try {
			log.info("Creating an admin with email '{}'", request.getEmail());
			userService.createAdmin(request);
			return ResponseEntity.status(HttpStatus.CREATED).body("Admin created successfully");
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/verify-email")
	public ResponseEntity<String> verifyEmail(@RequestBody EmailVerificationRequest req) {
		try {
			emailService.verifyAndPersistAccount(req);
			return ResponseEntity.ok("Email verified successfully");
		}
		catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
		}
	}

}
