package passwordmanager.exception;

public class InitialUserValueException extends RuntimeException {

    public InitialUserValueException(String message) {
        super(message);
    }
    public InitialUserValueException(String message, Exception e) {
        super(message,e);
    }
}