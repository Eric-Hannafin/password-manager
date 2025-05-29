package passwordmanager.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SecurityUtilTest {

    private static int EXPECTED_SALT_LENGTH = 24;

    @Test
    public void testSaltIsNotNullOrEmpty(){
        String salt = SecurityUtil.generateSalt();
        assertNotNull(salt);
        assertFalse(salt.isEmpty());
    }

    @Test
    public void testSaltLength(){
        String salt = SecurityUtil.generateSalt();
        assertEquals(EXPECTED_SALT_LENGTH, salt.length());
    }

    @Test
    public void testSaltValuesAreUnique(){
        String salt1 = SecurityUtil.generateSalt();
        String salt2 = SecurityUtil.generateSalt();
        assertNotEquals(salt1, salt2);
    }

}