package backend.service;

import backend.exception.NotificationException;
import backend.model.Notification;
import backend.model.Post;
import backend.model.User;
import backend.repository.NotificationRepository;
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

	public Notification createNotification(User user, String message) {
		Notification notification = new Notification();
		notification.setUser(user);
		notification.setMessage(message);
		notification.setTimestamp(LocalDateTime.now());
		notification.setReadStatus(false);
		return notificationRepository.save(notification);
	}

	public List<Notification> getNotificationsForUser(Long userId) {
		return notificationRepository.findByUserUserId(userId);
	}

	public void markAsRead(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new NotificationException("Notification not found with ID: " + notificationId));
		notification.setReadStatus(true);
		notificationRepository.save(notification);
	}

	public void markAllAsRead(Long userId) {
		List<Notification> notifications = notificationRepository.findByUserUserId(userId);
		for (Notification notification : notifications) {
			notification.setReadStatus(true);
		}
		notificationRepository.saveAll(notifications);
	}

	public void sendNewPostNotifications(Long communityId, Post post, List<User> members) {
		String notificationMessage = "New post in community: " + post.getCommunity().getName();
		for (User user : members) {
			Notification saved = createNotification(user, notificationMessage);
			messagingTemplate.convertAndSend("/topic/communities/" + communityId + "/user/" + user.getUserId(), saved);
		}
	}

}
