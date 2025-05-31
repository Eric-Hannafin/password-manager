package passwordmanager.exception;

public class FailedToLoadSQLResourceException extends Exception {

    public FailedToLoadSQLResourceException(String message, Exception e) {
        super(message, e);
    }
}