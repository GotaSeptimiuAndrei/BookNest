package backend.controller;

import backend.dto.CommunityMembershipDTO;
import backend.dto.ErrorDTO;
import backend.dto.response.APIResponse;
import backend.exception.CommunityMembershipException;
import backend.model.CommunityMembership;
import backend.model.User;
import backend.service.CommunityMembershipService;
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
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
@Slf4j
public class CommunityMembershipController {

	private static final String SUCCESS = "success";

	private static final String ERROR = "error";

	private final CommunityMembershipService communityMembershipService;

	@SecurityRequirement(name = "bearerAuth")
	@PostMapping
	public ResponseEntity<APIResponse<CommunityMembership>> joinCommunity(
			@Valid @RequestBody CommunityMembershipDTO dto) {

		CommunityMembership membership = communityMembershipService.joinCommunity(dto.getCommunityId(),
				dto.getUserId());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(APIResponse.<CommunityMembership>builder().status(SUCCESS).results(membership).build());
	}

	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping
	public ResponseEntity<APIResponse<Void>> leaveCommunity(@Valid @RequestBody CommunityMembershipDTO dto) {

		communityMembershipService.leaveCommunity(dto.getCommunityId(), dto.getUserId());

		return ResponseEntity.ok(APIResponse.<Void>builder().status(SUCCESS).build());
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<APIResponse<List<CommunityMembership>>> getAllMembershipsForUser(@PathVariable Long userId) {

		List<CommunityMembership> memberships = communityMembershipService.getAllMembershipsForUser(userId);

		return ResponseEntity
			.ok(APIResponse.<List<CommunityMembership>>builder().status(SUCCESS).results(memberships).build());
	}

	@GetMapping("/community/{communityId}")
	public ResponseEntity<APIResponse<List<User>>> getMembersOfCommunity(@PathVariable Long communityId) {

		List<User> members = communityMembershipService.getMembersOfCommunity(communityId);

		return ResponseEntity.ok(APIResponse.<List<User>>builder().status(SUCCESS).results(members).build());
	}

	@ExceptionHandler(CommunityMembershipException.class)
	public ResponseEntity<APIResponse<Void>> handleMembershipException(CommunityMembershipException ex) {
		log.error("Membership exception: {}", ex.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(APIResponse.<Void>builder()
				.status(ERROR)
				.errors(List.of(new ErrorDTO("membership", ex.getMessage())))
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
