package backend.config;

import backend.service.CustomUserDetailsService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private final RsaKeyProperties rsaKeyProperties;

	public SecurityConfiguration(RsaKeyProperties rsaKeyProperties) {
		this.rsaKeyProperties = rsaKeyProperties;
	}

	@Bean
	public AuthenticationManager authManager(CustomUserDetailsService customUserDetailsService) {
		var authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(customUserDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return new ProviderManager(authProvider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		grantedAuthoritiesConverter.setAuthorityPrefix("");

		JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
		authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return authenticationConverter;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.cors(c -> c.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth.requestMatchers("/actuator/health")
				.permitAll()
				.requestMatchers(HttpMethod.GET, "/api/books/**")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/api/books/**")
				.hasRole("ADMIN")
				.requestMatchers(HttpMethod.PATCH, "/api/books/**")
				.hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/books/**")
				.hasRole("ADMIN")

				.requestMatchers("/api/auth/**")
				.permitAll()

				.requestMatchers(HttpMethod.GET, "/api/reviews/**")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/api/reviews/**")
				.hasRole("USER")

				.requestMatchers("/api/loans/**")
				.hasRole("USER")

				.requestMatchers(HttpMethod.GET, "/api/communities/**")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/api/communities/**")
				.hasRole("AUTHOR")
				.requestMatchers(HttpMethod.PUT, "/api/communities/**")
				.hasRole("AUTHOR")

				.requestMatchers(HttpMethod.GET, "/api/memberships/**")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/api/memberships/**")
				.hasRole("USER")
				.requestMatchers(HttpMethod.DELETE, "/api/memberships/**")
				.hasRole("USER")

				.requestMatchers(HttpMethod.GET, "/api/posts/**")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/api/posts")
				.hasRole("AUTHOR")
				.requestMatchers(HttpMethod.DELETE, "/api/posts/**")
				.hasAnyRole("AUTHOR", "ADMIN")
				.requestMatchers(HttpMethod.POST, "/api/posts/*/like")
				.hasRole("USER")
				.requestMatchers(HttpMethod.POST, "/api/posts/*/unlike")
				.hasRole("USER")

				.requestMatchers(HttpMethod.GET, "/api/comments/**")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/api/comments")
				.hasAnyRole("USER", "AUTHOR")
				.requestMatchers(HttpMethod.POST, "/api/comments/*/reply")
				.hasAnyRole("USER", "AUTHOR")
				.requestMatchers(HttpMethod.DELETE, "/api/comments/**")
				.hasAnyRole("USER", "AUTHOR", "ADMIN")

				.requestMatchers("/api/notifications/**")
				.hasRole("USER")

				.requestMatchers("/api/authors/**")
				.permitAll()

				.requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/index.html",
						"/webjars/**", "/swagger-ui/**", "/swagger-resources", "/swagger-resources/**")
				.permitAll()
				.requestMatchers("/ws/**")
				.permitAll()
				.anyRequest()
				.authenticated())
			.oauth2ResourceServer(
					oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.httpBasic(Customizer.withDefaults())
			.build();
	}

	@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.publicKey()).build();
	}

	@Bean
	JwtEncoder jwtEncoder() {
		JWK jwk = new RSAKey.Builder(rsaKeyProperties.publicKey()).privateKey(rsaKeyProperties.privateKey()).build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);

	}

}
