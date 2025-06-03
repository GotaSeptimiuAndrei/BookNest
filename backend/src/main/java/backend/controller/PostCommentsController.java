package backend.controller;

import backend.dto.ErrorDTO;
import backend.dto.request.PostCommentRequest;
import backend.dto.response.APIResponse;
import backend.dto.response.PostCommentResponse;
import backend.exception.PostCommentsException;
import backend.service.PostCommentsService;
import backend.utils.JwtUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class PostCommentsController {

	private static final String SUCCESS = "success";

	private static final String ERROR = "error";

	private final PostCommentsService postCommentsService;

	@GetMapping("/post/{postId}")
	public ResponseEntity<APIResponse<List<PostCommentResponse>>> getCommentsForPost(@PathVariable Long postId) {
		List<PostCommentResponse> comments = postCommentsService.getCommentsForPost(postId);
		return ResponseEntity
			.ok(APIResponse.<List<PostCommentResponse>>builder().status(SUCCESS).results(comments).build());
	}

	@GetMapping("/{parentId}/children")
	public ResponseEntity<APIResponse<List<PostCommentResponse>>> getChildComments(@PathVariable Long parentId) {
		List<PostCommentResponse> childComments = postCommentsService.getChildComments(parentId);
		return ResponseEntity
			.ok(APIResponse.<List<PostCommentResponse>>builder().status(SUCCESS).results(childComments).build());
	}

	@SecurityRequirement(name = "bearerAuth")
	@PostMapping
	public ResponseEntity<APIResponse<PostCommentResponse>> createComment(@RequestHeader("Authorization") String token,
			@Valid @RequestBody PostCommentRequest request) {

		Long principalId = JwtUtils.extractPrincipalId(token);
		List<String> roles = JwtUtils.extractRoles(token);

		PostCommentResponse created = postCommentsService.createComment(request, principalId, roles);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(APIResponse.<PostCommentResponse>builder().status(SUCCESS).results(created).build());
	}

	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("/{parentId}/reply")
	public ResponseEntity<APIResponse<PostCommentResponse>> replyToComment(@RequestHeader("Authorization") String token,
			@PathVariable Long parentId, @Valid @RequestBody PostCommentRequest request) {

		Long principalId = JwtUtils.extractPrincipalId(token);
		List<String> roles = JwtUtils.extractRoles(token);

		PostCommentResponse reply = postCommentsService.replyToComment(parentId, request, principalId, roles);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(APIResponse.<PostCommentResponse>builder().status(SUCCESS).results(reply).build());
	}

	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(@RequestHeader("Authorization") String token,
			@PathVariable Long commentId) {

		Long principalId = JwtUtils.extractPrincipalId(token);
		List<String> roles = JwtUtils.extractRoles(token);

		postCommentsService.deleteComment(principalId, roles, commentId);

		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(PostCommentsException.class)
	public ResponseEntity<APIResponse<Void>> handlePostCommentsException(PostCommentsException ex) {
		log.error("PostComments exception occurred: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(APIResponse.<Void>builder()
				.status(ERROR)
				.errors(List.of(new ErrorDTO("comment", ex.getMessage())))
				.build());
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<APIResponse<Void>> handleBindException(BindException ex) {
		List<ErrorDTO> errorList = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(fieldError -> new ErrorDTO(fieldError.getField(), fieldError.getDefaultMessage()))
			.toList();

		return ResponseEntity.badRequest().body(APIResponse.<Void>builder().status(ERROR).errors(errorList).build());
	}

}
