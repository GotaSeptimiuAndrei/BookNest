package backend.utils;

import backend.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationCleanupJob {

	private final NotificationRepository notificationRepository;

	@Transactional
	@Scheduled(cron = "0 0 23 * * *")
	public void purgeExpiredVerifications() {
		notificationRepository.deleteAllByReadStatusIsTrue();
	}

}
