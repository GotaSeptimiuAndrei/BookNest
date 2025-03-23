package backend.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

	private static final JwtDecoder jwtDecoder;

	static {
		try {
			Resource resource = new ClassPathResource("certs/public_key.pem");
			try (InputStream inputStream = resource.getInputStream()) {
				// Convert the PEM file into a base64-encoded string (strip the
				// header/footer)
				String publicKeyPem = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
					.replace("-----BEGIN PUBLIC KEY-----", "")
					.replace("-----END PUBLIC KEY-----", "")
					.replaceAll("\\s+", "");

				// Decode the base64 content into a PublicKey object
				byte[] decoded = Base64.getDecoder().decode(publicKeyPem);
				X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

				// Create a JwtDecoder that can read/verify tokens with the given RSA
				// public key
				jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
			}
		}
		catch (IOException | GeneralSecurityException e) {
			throw new RuntimeException("Failed to initialize JwtDecoder with public key", e);
		}
	}

	public static String extractUsername(String bearerToken) {
		if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Invalid Authorization header.");
		}

		String token = bearerToken.substring(7);

		Jwt decodedJwt = jwtDecoder.decode(token);

		return decodedJwt.getClaimAsString("username");
	}

	public static boolean isTokenExpired(String bearerToken) {
		String token = bearerToken.substring(7);
		Jwt decodedJwt = jwtDecoder.decode(token);

		Instant expiresAt = decodedJwt.getExpiresAt();
		return expiresAt != null && Instant.now().isAfter(expiresAt);
	}

}
