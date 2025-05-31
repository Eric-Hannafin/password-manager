package passwordmanager.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SecretKeyFactoryExceptionTest {

    @Test
    void initializesWithMessageAndCause() {
        Exception cause = new Exception("Cause message");
        SecretKeyFactoryException exception = new SecretKeyFactoryException("Custom message", cause);

        Assertions.assertEquals("Custom message", exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }

    @Test
    void handlesNullCauseGracefully() {
        SecretKeyFactoryException exception = new SecretKeyFactoryException("Custom message", null);

        Assertions.assertEquals("Custom message", exception.getMessage());
        Assertions.assertNull(exception.getCause());
    }

    @Test
    void handlesNullMessageGracefully() {
        Exception cause = new Exception("Cause message");
        SecretKeyFactoryException exception = new SecretKeyFactoryException(null, cause);

        Assertions.assertNull(exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }
}