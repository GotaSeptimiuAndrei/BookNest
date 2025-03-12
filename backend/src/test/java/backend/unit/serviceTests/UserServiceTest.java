package backend.unit.serviceTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import backend.model.User;
import backend.model.EmailVerification;
import backend.repository.UserRepository;
import backend.repository.EmailVerificationRepository;
import backend.service.UserService;
import backend.service.EmailService;

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
		User user = new User();
		user.setUsername("john");
		user.setEmail("john@doe.com");
		user.setPassword("password");

		when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
		when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.empty());

		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

		User savedUser = new User();
		savedUser.setUserId(1L);
		savedUser.setUsername("john");
		savedUser.setEmail("john@doe.com");
		savedUser.setPassword("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		userService.registerUser(user);

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());
		assertThat(userCaptor.getValue().getPassword()).isEqualTo("encodedPassword");

		ArgumentCaptor<EmailVerification> verificationCaptor = ArgumentCaptor.forClass(EmailVerification.class);
		verify(emailVerificationRepository).save(verificationCaptor.capture());
		EmailVerification savedVerification = verificationCaptor.getValue();
		assertThat(savedVerification.getEmail()).isEqualTo("john@doe.com");
		assertThat(savedVerification.getVerificationCode()).isNotBlank();

		assertThat(savedVerification.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));

		verify(emailService).sendVerificationEmail(eq("john@doe.com"), anyString());
	}

	@Test
	void registerUser_usernameAlreadyExists_throwsException() {
		User user = new User();
		user.setUsername("john");
		user.setEmail("john@doe.com");

		when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User()));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> userService.registerUser(user));
		assertThat(ex.getMessage()).isEqualTo("Username already exists");

		verify(userRepository, never()).save(any(User.class));
		verify(emailVerificationRepository, never()).save(any());
		verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
	}

	@Test
	void registerUser_emailAlreadyExists_throwsException() {
		User user = new User();
		user.setUsername("john");
		user.setEmail("john@doe.com");

		when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
		when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.of(new User()));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> userService.registerUser(user));
		assertThat(ex.getMessage()).isEqualTo("Email already exists");

		verify(userRepository, never()).save(any(User.class));
		verify(emailVerificationRepository, never()).save(any());
		verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
	}

}
