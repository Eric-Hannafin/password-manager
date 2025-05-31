package passwordmanager.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

class CryptoServiceTest {

    @Test
    void encryptsAndDecryptsSuccessfullyWithValidInput() throws Exception {
        CryptoService cryptoService = new CryptoService();
        SecretKey key = KeyGenerator.getInstance("AES").generateKey();
        String plainText = "SensitiveData";

        String encrypted = cryptoService.encrypt(plainText, key);
        String decrypted = cryptoService.decrypt(encrypted, key);

        Assertions.assertEquals(plainText, decrypted);
    }

    @Test
    void throwsExceptionWhenDecryptingWithWrongKey() throws Exception {
        CryptoService cryptoService = new CryptoService();
        SecretKey key1 = KeyGenerator.getInstance("AES").generateKey();
        SecretKey key2 = KeyGenerator.getInstance("AES").generateKey();
        String plainText = "SensitiveData";

        String encrypted = cryptoService.encrypt(plainText, key1);

        Assertions.assertThrows(Exception.class, () -> cryptoService.decrypt(encrypted, key2));
    }

    @Test
    void throwsExceptionWhenDecryptingInvalidCipherText() throws Exception {
        CryptoService cryptoService = new CryptoService();
        SecretKey key = KeyGenerator.getInstance("AES").generateKey();
        String invalidCipherText = "InvalidCipherText";

        Assertions.assertThrows(Exception.class, () -> cryptoService.decrypt(invalidCipherText, key));
    }

    @Test
    void handlesEmptyPlainTextGracefully() throws Exception {
        CryptoService cryptoService = new CryptoService();
        SecretKey key = KeyGenerator.getInstance("AES").generateKey();
        String plainText = "";

        String encrypted = cryptoService.encrypt(plainText, key);
        String decrypted = cryptoService.decrypt(encrypted, key);

        Assertions.assertEquals(plainText, decrypted);
    }

    @Test
    void handlesNullPlainTextGracefully() throws Exception {
        CryptoService cryptoService = new CryptoService();
        SecretKey key = KeyGenerator.getInstance("AES").generateKey();

        Assertions.assertThrows(NullPointerException.class, () -> cryptoService.encrypt(null, key));
    }
}