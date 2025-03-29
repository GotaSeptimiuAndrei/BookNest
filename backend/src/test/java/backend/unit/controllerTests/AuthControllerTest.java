package backend.unit.controllerTests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Optional;

import backend.controller.AuthController;
import backend.dto.request.EmailVerificationRequest;
import backend.dto.request.LoginRequest;
import backend.dto.request.UserSignupRequest;
import backend.dto.response.TokenResponse;
import backend.model.EmailVerification;
import backend.repository.EmailVerificationRepository;
import backend.service.AuthorService;
import backend.service.TokenService;
import backend.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TokenService tokenService;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private AuthenticationManager authenticationManager;

	@MockitoBean
	private AuthorService authorService;

	@MockitoBean
	private EmailVerificationRepository verificationRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testLoginUser_Success() throws Exception {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("test@example.com");
		loginRequest.setPassword("password123");

		Authentication mockAuth = mock(Authentication.class);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);

		TokenResponse tokenResponse = new TokenResponse("some-jwt-token");
		when(tokenService.generateToken(mockAuth)).thenReturn(tokenResponse);

		mockMvc
			.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("some-jwt-token"));
	}

	@Test
	void testLoginUser_Unauthorized() throws Exception {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("test@example.com");
		loginRequest.setPassword("wrongPassword");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.thenThrow(new RuntimeException("Invalid login"));

		mockMvc
			.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void testRegisterUser_Success() throws Exception {
		UserSignupRequest signupRequest = new UserSignupRequest();
		signupRequest.setUsername("john123");
		signupRequest.setEmail("john@example.com");
		signupRequest.setPassword("secret");

		doNothing().when(userService).registerUser(any());

		mockMvc
			.perform(post("/api/auth/signup-user").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequest)))
			.andExpect(status().isCreated())
			.andExpect(content().string("User registered successfully"));
	}

	@Test
	void testRegisterUser_Conflict() throws Exception {
		UserSignupRequest signupRequest = new UserSignupRequest();
		signupRequest.setUsername("john123");
		signupRequest.setEmail("john@example.com");
		signupRequest.setPassword("secret");

		doThrow(new IllegalArgumentException("Username already exists")).when(userService).registerUser(any());

		mockMvc
			.perform(post("/api/auth/signup-user").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequest)))
			.andExpect(status().isConflict())
			.andExpect(content().string("Username already exists"));
	}

	@Test
	void testRegisterAuthor_Success() throws Exception {
		doNothing().when(authorService).registerAuthor(any());

		MockMultipartFile photoFile = new MockMultipartFile("photo", "photo.jpg", MediaType.IMAGE_JPEG_VALUE,
				"dummy-image-content".getBytes());

		mockMvc.perform(multipart("/api/auth/signup-author").file(photoFile)
			.param("fullName", "Jane Doe")
			.param("email", "jane@example.com")
			.param("password", "secret")
			.param("dateOfBirth", "2000-01-01")
			.param("city", "Example City")
			.param("country", "Example Country")
			.param("bio", "This is my short bio")
			// For multipart requests, set the method to POST explicitly:
			.with(request -> {
				request.setMethod("POST");
				return request;
			})).andExpect(status().isCreated()).andExpect(content().string("Author registered successfully"));
	}

	@Test
	void testRegisterAuthor_Conflict() throws Exception {
		doThrow(new IllegalArgumentException("Email already exists")).when(authorService).registerAuthor(any());

		MockMultipartFile photoFile = new MockMultipartFile("photo", "photo.jpg", MediaType.IMAGE_JPEG_VALUE,
				"dummy-image-content".getBytes());

		mockMvc.perform(multipart("/api/auth/signup-author").file(photoFile)
			.param("fullName", "Jane Doe")
			.param("email", "jane@example.com")
			.param("password", "secret")
			.param("dateOfBirth", "2000-01-01")
			.param("city", "Example City")
			.param("country", "Example Country")
			.param("bio", "This is my short bio")
			.with(request -> {
				request.setMethod("POST");
				return request;
			})).andExpect(status().isConflict()).andExpect(content().string("Email already exists"));
	}

	@Test
	void testVerifyEmail_Success() throws Exception {
		EmailVerificationRequest request = new EmailVerificationRequest();
		request.setEmail("test@example.com");
		request.setVerificationCode("1234");

		EmailVerification verification = new EmailVerification();
		verification.setEmail("test@example.com");
		verification.setVerificationCode("1234");
		verification.setVerified(false);
		verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));

		when(verificationRepository.findByEmailAndVerificationCodeAndVerifiedFalse("test@example.com", "1234"))
			.thenReturn(Optional.of(verification));

		mockMvc
			.perform(post("/api/auth/verify-email").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().string("Email verified successfully"));

		verify(verificationRepository).save(any(EmailVerification.class));
	}

	@Test
	void testVerifyEmail_InvalidOrAlreadyVerified() throws Exception {
		EmailVerificationRequest request = new EmailVerificationRequest();
		request.setEmail("test@example.com");
		request.setVerificationCode("9999");

		when(verificationRepository.findByEmailAndVerificationCodeAndVerifiedFalse("test@example.com", "9999"))
			.thenReturn(Optional.empty());

		mockMvc
			.perform(post("/api/auth/verify-email").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("Invalid code or already verified"));
	}

	@Test
	void testVerifyEmail_Expired() throws Exception {
		EmailVerificationRequest request = new EmailVerificationRequest();
		request.setEmail("test@example.com");
		request.setVerificationCode("1234");

		EmailVerification verification = new EmailVerification();
		verification.setEmail("test@example.com");
		verification.setVerificationCode("1234");
		verification.setVerified(false);
		verification.setExpiresAt(LocalDateTime.now().minusMinutes(1)); // expired

		when(verificationRepository.findByEmailAndVerificationCodeAndVerifiedFalse("test@example.com", "1234"))
			.thenReturn(Optional.of(verification));

		mockMvc
			.perform(post("/api/auth/verify-email").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("Verification code expired"));
	}

}
