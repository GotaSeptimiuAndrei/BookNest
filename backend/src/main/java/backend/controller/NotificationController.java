package backend.controller;

import backend.dto.ErrorDTO;
import backend.dto.response.APIResponse;
import backend.dto.response.NotificationResponse;
import backend.model.Notification;
import backend.service.NotificationService;
import backend.utils.JwtUtils;
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

	@GetMapping
	public ResponseEntity<APIResponse<List<NotificationResponse>>> getUnread(
			@RequestHeader("Authorization") String token) {

		Long userId = JwtUtils.extractPrincipalId(token);
		List<NotificationResponse> list = notificationService.findUnread(userId);

		return ResponseEntity
			.ok(APIResponse.<List<NotificationResponse>>builder().status(SUCCESS).results(list).build());
	}

	@PutMapping("/{id}/read")
	public ResponseEntity<Void> markOne(@RequestHeader("Authorization") String token, @PathVariable Long id) {

		notificationService.markOne(JwtUtils.extractPrincipalId(token), id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/read-all")
	public ResponseEntity<Void> markAll(@RequestHeader("Authorization") String token) {

		notificationService.markAll(JwtUtils.extractPrincipalId(token));
		return ResponseEntity.ok().build();
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
