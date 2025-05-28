package passwordmanager.security;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtil {

    public static byte[] generateSalt(){
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encode(salt);
    }

//    public static SecretKey generateKey(String password, byte[] salt){
//
//    }
}