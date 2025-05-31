package passwordmanager.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FailedToLoadSQLResourceExceptionTest {

    @Test
    void initializesWithMessageAndCause() {
        Exception cause = new Exception("Cause message");
        FailedToLoadSQLResourceException exception = new FailedToLoadSQLResourceException("Custom message", cause);

        Assertions.assertEquals("Custom message", exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }

    @Test
    void handlesNullCauseGracefully() {
        FailedToLoadSQLResourceException exception = new FailedToLoadSQLResourceException("Custom message", null);

        Assertions.assertEquals("Custom message", exception.getMessage());
        Assertions.assertNull(exception.getCause());
    }

    @Test
    void handlesNullMessageGracefully() {
        Exception cause = new Exception("Cause message");
        FailedToLoadSQLResourceException exception = new FailedToLoadSQLResourceException(null, cause);

        Assertions.assertNull(exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }
}