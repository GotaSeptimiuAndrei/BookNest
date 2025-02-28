package backend.service;

import backend.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final JwtEncoder encoder;

	private final UserService userService;

	public TokenResponse generateToken(Authentication authentication) {
		Instant now = Instant.now();

		String username = authentication.getName();
		var user = userService.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("User not found"));

		List<String> roles = authentication.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.toList();

		JwtClaimsSet claims = JwtClaimsSet.builder()
			.issuer("self")
			.issuedAt(now)
			.expiresAt(now.plus(1, ChronoUnit.HOURS))
			.subject(authentication.getName())
			.claim("roles", roles)
			.claim("userId", user.getUserId())
			.claim("email", user.getEmail())
			.build();

		return new TokenResponse(this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
	}

}
