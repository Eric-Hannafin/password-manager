package passwordmanager.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidateUsernameExceptionTest {
    @Test
    void initializesWithMessageAndCause() {
        Exception cause = new Exception("Cause message");
        ValidateUsernameException exception = new ValidateUsernameException("Custom message", cause);

        Assertions.assertEquals("Custom message", exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }

    @Test
    void handlesNullCauseGracefully() {
        ValidateUsernameException exception = new ValidateUsernameException("Custom message", null);

        Assertions.assertEquals("Custom message", exception.getMessage());
        Assertions.assertNull(exception.getCause());
    }

    @Test
    void handlesNullMessageGracefully() {
        Exception cause = new Exception("Cause message");
        ValidateUsernameException exception = new ValidateUsernameException(null, cause);

        Assertions.assertNull(exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }

}