package backend.unit.serviceTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import backend.dto.request.UserSignupRequest;
import backend.model.User;
import backend.model.EmailVerification;
import backend.repository.UserRepository;
import backend.repository.EmailVerificationRepository;
import backend.service.UserService;
import backend.service.EmailService;

import backend.utils.VerificationCodeGenerator;
import backend.utils.converter.UserConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private EmailVerificationRepository emailVerificationRepository;

	@Mock
	private EmailService emailService;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void registerUser_successfully() {
		UserSignupRequest request = new UserSignupRequest();
		request.setUsername("john");
		request.setEmail("john@doe.com");
		request.setPassword("password");

		when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
		when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.empty());

		User userEntity = new User();
		userEntity.setUsername("john");
		userEntity.setEmail("john@doe.com");
		userEntity.setPassword("password");

		try (MockedStatic<UserConverter> converterMock = Mockito.mockStatic(UserConverter.class);
				MockedStatic<VerificationCodeGenerator> codeGeneratorMock = Mockito
					.mockStatic(VerificationCodeGenerator.class)) {

			converterMock.when(() -> UserConverter.convertToEntity(request)).thenReturn(userEntity);
			when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

			codeGeneratorMock.when(VerificationCodeGenerator::generateVerificationCode).thenReturn("123456");

			when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
				User u = invocation.getArgument(0);
				u.setUserId(1L);
				return u;
			});

			userService.registerUser(request);

			ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
			verify(userRepository).save(userCaptor.capture());
			assertThat(userCaptor.getValue().getPassword()).isEqualTo("encodedPassword");

			ArgumentCaptor<EmailVerification> verificationCaptor = ArgumentCaptor.forClass(EmailVerification.class);
			verify(emailVerificationRepository).save(verificationCaptor.capture());
			EmailVerification savedVerification = verificationCaptor.getValue();

			assertThat(savedVerification.getEmail()).isEqualTo("john@doe.com");
			assertThat(savedVerification.getVerificationCode()).isEqualTo("123456");
			assertThat(savedVerification.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));

			verify(emailService).sendVerificationEmail(eq("john@doe.com"), eq("123456"));
		}
	}

	@Test
	void registerUser_usernameAlreadyExists_throwsException() {
		UserSignupRequest request = new UserSignupRequest();
		request.setUsername("john");
		request.setEmail("john@doe.com");

		when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User()));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> userService.registerUser(request));
		assertThat(ex.getMessage()).isEqualTo("Username already exists");

		verify(userRepository, never()).save(any());
		verify(emailVerificationRepository, never()).save(any());
		verify(emailService, never()).sendVerificationEmail(any(), any());
	}

	@Test
	void registerUser_emailAlreadyExists_throwsException() {
		UserSignupRequest request = new UserSignupRequest();
		request.setUsername("john");
		request.setEmail("john@doe.com");

		when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
		when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.of(new User()));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> userService.registerUser(request));
		assertThat(ex.getMessage()).isEqualTo("Email already exists");

		verify(userRepository, never()).save(any());
		verify(emailVerificationRepository, never()).save(any());
		verify(emailService, never()).sendVerificationEmail(any(), any());
	}

}
