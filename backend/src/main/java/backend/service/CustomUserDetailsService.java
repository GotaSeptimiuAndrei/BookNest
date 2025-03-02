package backend.service;

import backend.model.Author;
import backend.model.User;
import backend.repository.AuthorRepository;
import backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	private final AuthorRepository authorRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> maybeUser = userRepository.findByEmail(email);

		if (maybeUser.isPresent()) {
			User user = maybeUser.get();

			String role = Boolean.TRUE.equals(user.getIsAdmin()) ? "ADMIN" : "USER";

			return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
				.password(user.getPassword())
				.roles(role)
				.build();
		}

		Optional<Author> maybeAuthor = authorRepository.findByEmail(email);
		if (maybeAuthor.isPresent()) {
			Author author = maybeAuthor.get();

			return org.springframework.security.core.userdetails.User.withUsername(author.getEmail())
				.password(author.getPassword())
				.roles("AUTHOR")
				.build();
		}

		throw new UsernameNotFoundException("No user or author found for email: " + email);
	}

}
