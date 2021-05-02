package sk.tuke.kpi.kp.gamestudio.service.exceptions;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
