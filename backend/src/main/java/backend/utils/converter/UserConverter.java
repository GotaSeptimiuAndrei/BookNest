package backend.utils.converter;

import backend.dto.request.UserRequestDTO;
import backend.model.User;

public class UserConverter {

	public static User convertToEntity(UserRequestDTO userDTO) {
		User user = new User();
		user.setEmail(userDTO.getEmail());
		user.setPassword(userDTO.getPassword());
		user.setIsAdmin(userDTO.getIsAdmin());
		user.setUsername(userDTO.getUsername());
		return user;
	}

}
