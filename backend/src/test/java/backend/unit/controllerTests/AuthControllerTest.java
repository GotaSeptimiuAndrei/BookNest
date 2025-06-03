package backend.unit.controllerTests;

import backend.controller.AuthController;
import backend.dto.request.*;
import backend.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private TokenService tokenService;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private AuthenticationManager authenticationManager;

	@MockitoBean
	private AuthorService authorService;

	@MockitoBean
	private EmailService emailService;

	@Test
	void loginUser_success() throws Exception {
		LoginRequest req = new LoginRequest("test@example.com", "password123");

		Authentication auth = Mockito.mock(Authentication.class);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
		when(tokenService.generateToken(auth)).thenReturn("jwt‑token");

		mockMvc
			.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isOk())
			.andExpect(content().string("jwt‑token"));
	}

	@Test
	void loginUser_failure() throws Exception {
		LoginRequest req = new LoginRequest("test@example.com", "wrong");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.thenThrow(new BadCredentialsException("Invalid email or password"));

		mockMvc
			.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string("Invalid email or password"));
	}

	@Test
	void registerUser_success() throws Exception {
		UserSignupRequest req = new UserSignupRequest("john@example.com", "secret", "john123");

		doNothing().when(userService).registerUser(any());

		mockMvc
			.perform(post("/api/auth/signup-user").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isCreated())
			.andExpect(content().string("User registered successfully"));
	}

	@Test
	void registerUser_conflict() throws Exception {
		UserSignupRequest req = new UserSignupRequest("john@example.com", "secret", "john123");

		doThrow(new IllegalArgumentException("Username already exists")).when(userService).registerUser(any());

		mockMvc
			.perform(post("/api/auth/signup-user").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isConflict())
			.andExpect(content().string("Username already exists"));
	}

	@Test
	void registerAuthor_success() throws Exception {
		doNothing().when(authorService).registerAuthor(any());

		MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", MediaType.IMAGE_JPEG_VALUE,
				"img".getBytes());

		mockMvc.perform(multipart("/api/auth/signup-author").file(photo)
			.param("fullName", "Jane Doe")
			.param("email", "jane@example.com")
			.param("password", "secret")
			.param("dateOfBirth", "2000-01-01")
			.param("city", "City")
			.param("country", "Country")
			.param("bio", "Short bio")
			.with(req -> {
				req.setMethod("POST");
				return req;
			})).andExpect(status().isCreated()).andExpect(content().string("Author registered successfully"));
	}

	@Test
	void registerAuthor_conflict() throws Exception {
		doThrow(new IllegalArgumentException("Email already exists")).when(authorService).registerAuthor(any());

		MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", MediaType.IMAGE_JPEG_VALUE,
				"img".getBytes());

		mockMvc.perform(multipart("/api/auth/signup-author").file(photo)
			.param("fullName", "Jane Doe")
			.param("email", "jane@example.com")
			.param("password", "secret")
			.param("dateOfBirth", "2000-01-01")
			.param("city", "City")
			.param("country", "Country")
			.param("bio", "Short bio")
			.with(req -> {
				req.setMethod("POST");
				return req;
			})).andExpect(status().isConflict()).andExpect(content().string("Email already exists"));
	}

	@Test
	void verifyEmail_success() throws Exception {
		EmailVerificationRequest req = new EmailVerificationRequest("test@example.com", "1234");

		doNothing().when(emailService).verifyAndPersistAccount(any());

		mockMvc
			.perform(post("/api/auth/verify-email").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isOk())
			.andExpect(content().string("Email verified successfully"));

		verify(emailService).verifyAndPersistAccount(any());
	}

	@Test
	void verifyEmail_invalid() throws Exception {
		EmailVerificationRequest req = new EmailVerificationRequest("test@example.com", "9999");

		doThrow(new IllegalArgumentException("Invalid code or already verified")).when(emailService)
			.verifyAndPersistAccount(any());

		mockMvc
			.perform(post("/api/auth/verify-email").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("Invalid code or already verified"));
	}

	@Test
	void verifyEmail_expired() throws Exception {
		EmailVerificationRequest req = new EmailVerificationRequest("test@example.com", "1234");

		doThrow(new IllegalArgumentException("Verification code expired")).when(emailService)
			.verifyAndPersistAccount(any());

		mockMvc
			.perform(post("/api/auth/verify-email").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("Verification code expired"));
	}

}
