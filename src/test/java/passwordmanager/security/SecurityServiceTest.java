package passwordmanager.security;

import org.junit.jupiter.api.Test;
import passwordmanager.exception.DeriveUserKeyException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityServiceTest {

    private static int EXPECTED_SALT_LENGTH = 24;

    @Test
    public void testSaltIsNotNullOrEmpty(){
        String salt = SecurityService.generateSalt();
        assertNotNull(salt);
        assertFalse(salt.isEmpty());
    }

    @Test
    public void testSaltLength(){
        String salt = SecurityService.generateSalt();
        assertEquals(EXPECTED_SALT_LENGTH, salt.length());
    }
    // trigger build
    @Test
    public void testSaltValuesAreUnique(){
        String salt1 = SecurityService.generateSalt();
        String salt2 = SecurityService.generateSalt();
        assertNotEquals(salt1, salt2);
    }

    @Test
    void deriveUserKey_returnsValidKey() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecurityService service = new SecurityService(factory);

        SecretKey key = service.deriveUserKey("testName", "salt");

        assertNotNull(key);
        assertEquals("AES", key.getAlgorithm());
    }

    @Test
    void deriveUserKey_throwsCustomExceptionOnFailure() throws Exception {
        SecretKeyFactory mockFactory = mock(SecretKeyFactory.class);
        when(mockFactory.generateSecret(any())).thenThrow(new InvalidKeySpecException("Test failure"));

        SecurityService service = new SecurityService(mockFactory);

        DeriveUserKeyException ex = assertThrows(
                DeriveUserKeyException.class,
                () -> service.deriveUserKey("testName", "salt")
        );

        assertTrue(ex.getCause() instanceof InvalidKeySpecException);
    }

}