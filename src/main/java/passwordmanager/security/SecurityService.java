package passwordmanager.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.exception.DeriveUserKeyException;
import passwordmanager.exception.SecretKeyFactoryException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class SecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);
    private final SecretKeyFactory secretKeyFactory;

    public SecurityService() {
        try {
            secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Failed to create security factory instance", e);
            throw new SecretKeyFactoryException("Failed to create security factory instance", e);
        }
    }

    public SecurityService(SecretKeyFactory secretKeyFactory) {
        this.secretKeyFactory = secretKeyFactory;
    }

    public static String generateSalt(){
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public SecretKey deriveUserKey(String password, String salt) throws DeriveUserKeyException {
        try {
            byte[] bytes = salt.getBytes();

            int iterations = 65536;
            int keyLength = 256;

            KeySpec keyspec = new PBEKeySpec(password.toCharArray(), bytes, iterations, keyLength);

            byte[] encoded = secretKeyFactory.generateSecret(keyspec).getEncoded();

            return new SecretKeySpec(encoded, "AES");

        } catch (Exception e) {
            throw new DeriveUserKeyException("Failed to derive key for user", e);
        }
    }
}