package passwordmanager.exception;

public class SecretKeyFactoryException extends RuntimeException{
    public SecretKeyFactoryException(String message, Exception e) {
        super(message,e);
    }
}