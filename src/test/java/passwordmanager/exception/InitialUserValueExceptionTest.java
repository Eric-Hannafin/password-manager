package passwordmanager.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InitialUserValueExceptionTest {

    @Test
    void initializesWithMessageCorrectly() {
        InitialUserValueException exception = new InitialUserValueException("Initialization failed");

        Assertions.assertEquals("Initialization failed", exception.getMessage());
    }

    @Test
    void initializesWithMessageAndCauseCorrectly() {
        Exception cause = new Exception("Cause of the failure");
        InitialUserValueException exception = new InitialUserValueException("Initialization failed", cause);

        Assertions.assertEquals("Initialization failed", exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }

    @Test
    void handlesNullMessageGracefully() {
        InitialUserValueException exception = new InitialUserValueException(null);

        Assertions.assertNull(exception.getMessage());
    }

    @Test
    void handlesNullCauseGracefully() {
        InitialUserValueException exception = new InitialUserValueException("Initialization failed", null);

        Assertions.assertEquals("Initialization failed", exception.getMessage());
        Assertions.assertNull(exception.getCause());
    }
}