package passwordmanager.exception;

public class DeriveUserKeyException extends Exception {
    public DeriveUserKeyException(String message, Exception e) {
        super(message,e);
    }
}