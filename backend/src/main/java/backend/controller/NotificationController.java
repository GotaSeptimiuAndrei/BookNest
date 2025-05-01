package backend.controller;

import backend.dto.ErrorDTO;
import backend.dto.response.APIResponse;
import backend.model.Notification;
import backend.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

	private static final String SUCCESS = "success";

	private static final String ERROR = "error";

	private final NotificationService notificationService;

	@GetMapping("/user/{userId}")
	public ResponseEntity<APIResponse<List<Notification>>> getNotificationsForUser(@PathVariable Long userId) {
		List<Notification> notifications = notificationService.getNotificationsForUser(userId);
		return ResponseEntity
			.ok(APIResponse.<List<Notification>>builder().status(SUCCESS).results(notifications).build());
	}

	@PostMapping("/{notificationId}/read")
	public ResponseEntity<APIResponse<Void>> markNotificationAsRead(@PathVariable Long notificationId) {
		notificationService.markAsRead(notificationId);
		return ResponseEntity.ok(APIResponse.<Void>builder().status(SUCCESS).build());
	}

	@PostMapping("/user/{userId}/read-all")
	public ResponseEntity<APIResponse<Void>> markAllNotificationsAsRead(@PathVariable Long userId) {
		notificationService.markAllAsRead(userId);
		return ResponseEntity.ok(APIResponse.<Void>builder().status(SUCCESS).build());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<APIResponse<Void>> handleException(RuntimeException ex) {
		log.error("Notification exception occurred: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(APIResponse.<Void>builder()
				.status(ERROR)
				.errors(List.of(new ErrorDTO("notification", ex.getMessage())))
				.build());
	}

}
