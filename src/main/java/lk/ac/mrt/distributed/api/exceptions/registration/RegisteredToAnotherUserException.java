package lk.ac.mrt.distributed.api.exceptions.registration;

/**
 * @author Chathura Widanage
 */
public class RegisteredToAnotherUserException extends RegistrationException {
    public RegisteredToAnotherUserException(String s) {
        super(s);
    }
}
