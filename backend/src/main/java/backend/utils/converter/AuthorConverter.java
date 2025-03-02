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
		author.setPhoto(authorDTO.getPhoto());
		author.setDateOfBirth(authorDTO.getDateOfBirth());
		return author;
	}

}
