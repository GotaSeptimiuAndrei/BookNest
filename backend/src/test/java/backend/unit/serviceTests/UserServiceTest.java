package backend.unit.serviceTests;

import backend.dto.request.UserSignupRequest;
import backend.model.EmailVerification;
import backend.model.User;
import backend.repository.EmailVerificationRepository;
import backend.repository.UserRepository;
import backend.service.EmailService;
import backend.service.UserService;
import backend.utils.VerificationCodeGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder; // still injected in service

	@Mock
	private EmailVerificationRepository emailVerificationRepository;

	@Mock
	private EmailService emailService;

	@InjectMocks
	private UserService userService;

	@Test
	void registerUser_successfully() throws JsonProcessingException {

		UserSignupRequest req = new UserSignupRequest("john@doe.com", "password", "john");

		when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.empty());
		when(emailVerificationRepository.existsByEmailAndVerifiedFalse("john@doe.com")).thenReturn(false);

		try (MockedStatic<VerificationCodeGenerator> codeGen = Mockito.mockStatic(VerificationCodeGenerator.class)) {

			codeGen.when(VerificationCodeGenerator::generateVerificationCode).thenReturn("123456");

			userService.registerUser(req);

			verify(userRepository, never()).save(any(User.class));

			ArgumentCaptor<EmailVerification> vCaptor = ArgumentCaptor.forClass(EmailVerification.class);

			verify(emailVerificationRepository).save(vCaptor.capture());
			EmailVerification saved = vCaptor.getValue();

			assertThat(saved.getEmail()).isEqualTo("john@doe.com");
			assertThat(saved.getVerificationCode()).isEqualTo("123456");
			assertThat(saved.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));

			verify(emailService).sendVerificationEmail("john@doe.com", "123456");
		}
	}

	@Test
	void registerUser_emailAlreadyActive_throwsException() {

		UserSignupRequest req = new UserSignupRequest("john@doe.com", "password", "john");

		when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.of(new User()));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(req));

		assertThat(ex.getMessage()).isEqualTo("Email already exists");

		verify(emailVerificationRepository, never()).save(any());
		verify(emailService, never()).sendVerificationEmail(any(), any());
	}

	@Test
	void registerUser_emailPendingVerification_throwsException() {

		UserSignupRequest req = new UserSignupRequest("john@doe.com", "password", "john");

		when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.empty());
		when(emailVerificationRepository.existsByEmailAndVerifiedFalse("john@doe.com")).thenReturn(true);

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(req));

		assertThat(ex.getMessage()).isEqualTo("A verification code was already sent to this email");

		verify(emailVerificationRepository, never()).save(any());
		verify(emailService, never()).sendVerificationEmail(any(), any());
	}

}
