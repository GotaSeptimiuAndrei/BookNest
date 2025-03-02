package backend.utils.converter;

import backend.dto.request.UserSignupRequest;
import backend.model.User;

public class UserConverter {

	public static User convertToEntity(UserSignupRequest userDTO) {
		User user = new User();
		user.setEmail(userDTO.getEmail());
		user.setPassword(userDTO.getPassword());
		user.setIsAdmin(false);
		user.setUsername(userDTO.getUsername());
		return user;
	}

}
