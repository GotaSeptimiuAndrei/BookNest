package backend.utils.converter;

import backend.dto.request.AuthorSignupRequest;
import backend.model.Author;

public class AuthorConverter {

	public static Author convertToEntity(AuthorSignupRequest authorDTO) {
		Author author = new Author();
		author.setFullName(authorDTO.getFullName());
		author.setEmail(authorDTO.getEmail());
		author.setPassword(authorDTO.getPassword());
		author.setCity(authorDTO.getCity());
		author.setCountry(authorDTO.getCountry());
		author.setBio(authorDTO.getBio());
		// convert to s3 bucket link
		author.setPhoto("https://source.unsplash.com/300x300/?book");
		author.setDateOfBirth(authorDTO.getDateOfBirth());
		return author;
	}

}
