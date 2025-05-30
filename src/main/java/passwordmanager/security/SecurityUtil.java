package passwordmanager.security;

import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static String generateSalt(){
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}