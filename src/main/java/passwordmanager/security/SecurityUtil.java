package passwordmanager.security;

import passwordmanager.exception.DeriveUserKeyException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static String generateSalt(){
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static SecretKey deriveUserKey(String username, String salt) throws NoSuchAlgorithmException, DeriveUserKeyException {
        try {
            byte[] bytes = salt.getBytes();

            int iterations = 65536;
            int keyLength = 256;

            KeySpec keyspec = new PBEKeySpec(username.toCharArray(), bytes, iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] encoded = factory.generateSecret(keyspec).getEncoded();

            return new SecretKeySpec(encoded, "AES");

        } catch (Exception e) {
            throw new DeriveUserKeyException("Failed to derive key for user", e);
        }
    }
}