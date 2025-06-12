package backend.service;

import backend.dto.response.NotificationResponse;
import backend.model.Notification;
import backend.model.Post;
import backend.model.User;
import backend.repository.NotificationRepository;
import backend.utils.converter.NotificationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	private final SimpMessagingTemplate messagingTemplate;

	public Notification createNotification(User user, Long communityId, String message) {
		Notification notification = new Notification();
		notification.setUser(user);
		notification.setCommunityId(communityId);

		notification.setMessage(message);
		notification.setTimestamp(LocalDateTime.now());
		notification.setReadStatus(false);
		return notificationRepository.save(notification);
	}

	public List<NotificationResponse> findUnread(Long userId) {
		return notificationRepository.findByUserUserIdAndReadStatusFalse(userId)
			.stream()
			.map(NotificationConverter::convertToDto)
			.toList();
	}

	public void markOne(Long userId, Long notifId) {
		Notification n = notificationRepository.findByNotificationIdAndUserUserId(notifId, userId)
			.orElseThrow(() -> new RuntimeException("Notification not found"));
		if (!n.isReadStatus()) {
			n.setReadStatus(true);
			notificationRepository.save(n);
		}
	}

	public void markAll(Long userId) {
		notificationRepository.markAllRead(userId);
	}

	public void sendNewPostNotifications(Long communityId, Post post, List<User> members) {

		String msg = "New post in community: " + post.getCommunity().getName();

		for (User u : members) {
			Notification saved = createNotification(u, communityId, msg);

			NotificationResponse dto = NotificationConverter.convertToDto(saved);

			messagingTemplate.convertAndSend("/queue/notifications-" + u.getUserId(), dto);
		}
	}

}
