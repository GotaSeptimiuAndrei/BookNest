package backend.controller;

import backend.dto.ErrorDTO;
import backend.dto.request.PostCommentRequest;
import backend.dto.response.APIResponse;
import backend.dto.response.PostCommentResponse;
import backend.exception.PostCommentsException;
import backend.service.PostCommentsService;
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
	public ResponseEntity<APIResponse<PostCommentResponse>> createComment(
			@Valid @RequestBody PostCommentRequest request) {
		PostCommentResponse createdComment = postCommentsService.createComment(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(APIResponse.<PostCommentResponse>builder().status(SUCCESS).results(createdComment).build());
	}

	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("/{parentId}/reply")
	public ResponseEntity<APIResponse<PostCommentResponse>> replyToComment(@PathVariable Long parentId,
			@Valid @RequestBody PostCommentRequest request) {
		PostCommentResponse reply = postCommentsService.replyToComment(parentId, request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(APIResponse.<PostCommentResponse>builder().status(SUCCESS).results(reply).build());
	}

	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping("/{commentId}")
	public ResponseEntity<APIResponse<Void>> deleteComment(@PathVariable Long commentId) {
		postCommentsService.deleteComment(commentId);
		return ResponseEntity.ok(APIResponse.<Void>builder().status(SUCCESS).build());
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
