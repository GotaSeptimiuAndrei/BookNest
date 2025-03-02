package backend.service;

import backend.model.Author;
import backend.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

	private final AuthorRepository authorRepository;

	private final PasswordEncoder passwordEncoder;

	public void saveAuthor(Author author) {
		if (authorRepository.findByEmail(author.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}

		author.setPassword(passwordEncoder.encode(author.getPassword()));

		authorRepository.save(author);
	}

	public Optional<Author> findByEmail(String email) {
		return authorRepository.findByEmail(email);
	}

}
