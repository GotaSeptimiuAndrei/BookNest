package backend.controller;

import backend.dto.ErrorDTO;
import backend.dto.request.PostRequest;
import backend.dto.response.APIResponse;
import backend.dto.response.PostResponse;
import backend.exception.PostException;
import backend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

	private static final String SUCCESS = "success";

	private static final String ERROR = "error";

	private final PostService postService;

	@GetMapping("/{id}")
	public ResponseEntity<APIResponse<PostResponse>> getPostById(@PathVariable Long id) {
		PostResponse postResponse = postService.getPostById(id);
		return ResponseEntity.ok(APIResponse.<PostResponse>builder().status(SUCCESS).results(postResponse).build());
	}

	@GetMapping("/community/{communityId}")
	public ResponseEntity<APIResponse<List<PostResponse>>> getPostsByCommunityId(@PathVariable Long communityId) {
		List<PostResponse> posts = postService.getPostsByCommunityId(communityId);
		return ResponseEntity.ok(APIResponse.<List<PostResponse>>builder().status(SUCCESS).results(posts).build());
	}

	@GetMapping("/community/{communityId}/paginated")
	public ResponseEntity<APIResponse<Page<PostResponse>>> getPostsByCommunityIdPaginated(
			@PathVariable Long communityId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Page<PostResponse> postPage = postService.getPostsByCommunityIdPaginated(communityId, page, size);
		return ResponseEntity.ok(APIResponse.<Page<PostResponse>>builder().status(SUCCESS).results(postPage).build());
	}

	@PostMapping
	public ResponseEntity<APIResponse<PostResponse>> createPost(@Valid @ModelAttribute PostRequest postRequest) {
		PostResponse createdPost = postService.createPost(postRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(APIResponse.<PostResponse>builder().status(SUCCESS).results(createdPost).build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<APIResponse<Void>> deletePost(@PathVariable Long id) {
		postService.deletePost(id);
		return ResponseEntity.ok(APIResponse.<Void>builder().status(SUCCESS).build());
	}

	@PostMapping("/{id}/like")
	public ResponseEntity<APIResponse<Void>> likePost(@PathVariable Long id, @RequestParam Long userId) {
		postService.likePost(id, userId);
		return ResponseEntity.ok(APIResponse.<Void>builder().status(SUCCESS).build());
	}

	@PostMapping("/{id}/unlike")
	public ResponseEntity<APIResponse<Void>> unlikePost(@PathVariable Long id, @RequestParam Long userId) {
		postService.unlikePost(id, userId);
		return ResponseEntity.ok(APIResponse.<Void>builder().status(SUCCESS).build());
	}

	@ExceptionHandler(PostException.class)
	public ResponseEntity<APIResponse<Void>> handlePostException(PostException ex) {
		log.error("Post exception occurred: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(APIResponse.<Void>builder()
				.status(ERROR)
				.errors(List.of(new ErrorDTO("post", ex.getMessage())))
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
