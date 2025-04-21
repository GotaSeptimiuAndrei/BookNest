package backend.utils.converter;

import backend.dto.request.AuthorSignupRequest;
import backend.dto.response.AuthorResponse;
import backend.model.Author;

public class AuthorConverter {

	public static Author convertToEntity(AuthorSignupRequest authorDTO, String photoUrl) {
		Author author = new Author();
		author.setFullName(authorDTO.getFullName());
		author.setEmail(authorDTO.getEmail());
		author.setPassword(authorDTO.getPassword());
		author.setCity(authorDTO.getCity());
		author.setCountry(authorDTO.getCountry());
		author.setBio(authorDTO.getBio());
		author.setPhoto(photoUrl);
		author.setDateOfBirth(authorDTO.getDateOfBirth());
		return author;
	}

	public static AuthorResponse convertToDto(Author author) {
		AuthorResponse dto = new AuthorResponse();
		dto.setAuthorId(author.getAuthorId());
		dto.setFullName(author.getFullName());
		dto.setEmail(author.getEmail());
		dto.setDateOfBirth(author.getDateOfBirth());
		dto.setCity(author.getCity());
		dto.setCountry(author.getCountry());
		dto.setBio(author.getBio());
		dto.setPhoto(author.getPhoto());
		return dto;
	}

}
