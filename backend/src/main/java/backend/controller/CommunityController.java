package backend.controller;

import backend.dto.CommunityDTO;
import backend.dto.ErrorDTO;
import backend.dto.response.APIResponse;
import backend.exception.CommunityException;
import backend.model.Community;
import backend.service.CommunityService;
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
@RequestMapping("/api/communities")
@RequiredArgsConstructor
@Slf4j
public class CommunityController {

	private static final String SUCCESS = "success";

	private static final String ERROR = "error";

	private final CommunityService communityService;

	@GetMapping("/{id}")
	public ResponseEntity<APIResponse<Community>> getCommunityById(@PathVariable Long id) {
		Community community = communityService.getCommunityById(id);

		return ResponseEntity.ok(APIResponse.<Community>builder().status(SUCCESS).results(community).build());
	}

	@GetMapping("/author/{authorId}")
	public ResponseEntity<APIResponse<Community>> getCommunityByAuthor(@PathVariable Long authorId) {
		Community community = communityService.getCommunityByAuthor(authorId);

		return ResponseEntity.ok(APIResponse.<Community>builder().status(SUCCESS).results(community).build());
	}

	@GetMapping
	public ResponseEntity<APIResponse<List<Community>>> getAllCommunities() {
		List<Community> communities = communityService.getAllCommunities();

		return ResponseEntity.ok(APIResponse.<List<Community>>builder().status(SUCCESS).results(communities).build());
	}

	@SecurityRequirement(name = "bearerAuth")
	@PostMapping
	public ResponseEntity<APIResponse<Community>> createCommunity(@Valid @ModelAttribute CommunityDTO communityDTO) {
		Community createdCommunity = communityService.createCommunity(communityDTO);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(APIResponse.<Community>builder().status(SUCCESS).results(createdCommunity).build());
	}

	@SecurityRequirement(name = "bearerAuth")
	@PutMapping
	public ResponseEntity<APIResponse<Community>> updateCommunity(@Valid @ModelAttribute CommunityDTO communityDTO) {
		Community updatedCommunity = communityService.updateCommunity(communityDTO);

		return ResponseEntity.ok(APIResponse.<Community>builder().status(SUCCESS).results(updatedCommunity).build());
	}

	@ExceptionHandler(CommunityException.class)
	public ResponseEntity<APIResponse<Void>> handleCommunityException(CommunityException ex) {
		log.error("Community exception occurred: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(APIResponse.<Void>builder()
				.status(ERROR)
				.errors(List.of(new ErrorDTO("community", ex.getMessage())))
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
