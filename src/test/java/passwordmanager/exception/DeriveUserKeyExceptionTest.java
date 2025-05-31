package passwordmanager.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DeriveUserKeyExceptionTest {

    @Test
    void initializesWithMessageAndCause() {
        Exception cause = new Exception("Cause message");
        DeriveUserKeyException exception = new DeriveUserKeyException("Custom message", cause);

        Assertions.assertEquals("Custom message", exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }

    @Test
    void handlesNullCauseGracefully() {
        DeriveUserKeyException exception = new DeriveUserKeyException("Custom message", null);

        Assertions.assertEquals("Custom message", exception.getMessage());
        Assertions.assertNull(exception.getCause());
    }

    @Test
    void handlesNullMessageGracefully() {
        Exception cause = new Exception("Cause message");
        DeriveUserKeyException exception = new DeriveUserKeyException(null, cause);

        Assertions.assertNull(exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }
}