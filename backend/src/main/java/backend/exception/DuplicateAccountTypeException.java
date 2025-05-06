package backend.exception;

public class DuplicateAccountTypeException extends RuntimeException {

	public DuplicateAccountTypeException(String email) {
		super("The email '" + email + "' is registered both as a User and an Author.");
	}

}