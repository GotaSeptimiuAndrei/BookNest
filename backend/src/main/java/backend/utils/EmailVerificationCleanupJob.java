package backend.utils;

import backend.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EmailVerificationCleanupJob {

	private final EmailVerificationRepository verificationRepository;

	@Scheduled(cron = "0 0 * * * *")
	public void purgeExpiredVerifications() {
		verificationRepository.deleteAllByVerifiedFalseAndExpiresAtBefore(LocalDateTime.now());
	}

}
