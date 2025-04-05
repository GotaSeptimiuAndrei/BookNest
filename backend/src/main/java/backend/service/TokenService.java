package backend.service;

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

	private final AuthorService authorService;

	public String generateToken(Authentication authentication) {
		Instant now = Instant.now();

		String email = authentication.getName();

		List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		JwtClaimsSet claims;

		if (roles.contains("ROLE_AUTHOR")) {
			var author = authorService.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("Author not found"));
			claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(now)
				.expiresAt(now.plus(1, ChronoUnit.HOURS))
				.subject(email)
				.claim("roles", roles)
				.claim("authorId", author.getAuthorId())
				.claim("fullName", author.getFullName())
				.build();
		}
		else {
			var user = userService.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
			claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(now)
				.expiresAt(now.plus(1, ChronoUnit.HOURS))
				.subject(email)
				.claim("roles", roles)
				.claim("userId", user.getUserId())
				.claim("username", user.getUsername())
				.build();
		}

		return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

}
