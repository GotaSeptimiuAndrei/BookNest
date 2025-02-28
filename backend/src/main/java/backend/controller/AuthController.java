package backend.controller;

import backend.dto.request.LoginRequest;
import backend.dto.request.UserRequestDTO;
import backend.utils.converter.UserConverter;
import backend.dto.response.TokenResponse;
import backend.service.TokenService;
import backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final TokenService tokenService;

	private final UserService userService;

	private final AuthenticationManager authenticationManager;

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginRequest loginRequest) {
		try {
			log.info("User {} is trying to login", loginRequest.getUsername());
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
			log.info("Authentication successful");
			log.info("User {} has authorities: {}", authentication.getName(), authentication.getAuthorities());
			return ResponseEntity.ok(tokenService.generateToken(authentication));
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<String> registerUser(@RequestBody UserRequestDTO userDTO) {
		try {
			log.info("User '{}' is trying to register", userDTO.getUsername());
			var user = UserConverter.convertToEntity(userDTO);
			userService.saveUser(user);
			log.info("User '{}' registered successfully", user.getUsername());
			return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

}
