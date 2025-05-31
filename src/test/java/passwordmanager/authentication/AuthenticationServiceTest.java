package passwordmanager.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passwordmanager.database.DatabaseService;
import passwordmanager.exception.ValidateUsernameException;
import passwordmanager.security.CryptoService;
import passwordmanager.security.SecurityService;

import javax.crypto.SecretKey;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    private DatabaseService mockDb;
    private SecurityService mockSec;
    private CryptoService mockCrypto;
    private Scanner mockScanner;
    private AuthenticationService service;

    @BeforeEach
    void setup() {
        mockDb = mock(DatabaseService.class);
        mockSec = mock(SecurityService.class);
        mockCrypto = mock(CryptoService.class);
    }

    @Test
    void testCreateInitialValue_encryptionFlow() throws Exception {
        SecretKey mockKey = mock(SecretKey.class);
        when(mockSec.deriveUserKey("user1", "salt1")).thenReturn(mockKey);
        when(mockCrypto.encrypt("INITIAL_SITE", mockKey)).thenReturn("encrypted");

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, new Scanner(System.in));
        service.createInitialValue("user1", "salt1");

        verify(mockDb).saveUserValue("INITIAL_SITE", "user1", "encrypted");
    }

    @Test
    void testCheckUsernameAvailability_acceptsValidUsername() throws ValidateUsernameException {
        String input = "user123\n";
        mockScanner = new Scanner(input);
        when(mockDb.checkIfUserAlreadyExists("user123")).thenReturn(false);

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, mockScanner);
        String result = service.checkUsernameAvailability();

        assertEquals("user123", result);
    }

    @Test
    void testCheckUsernameAvailability_rejectsAndRetries() throws ValidateUsernameException {
        String input = "taken\nuserX\n";
        mockScanner = new Scanner(input);
        when(mockDb.checkIfUserAlreadyExists("taken")).thenReturn(true);
        when(mockDb.checkIfUserAlreadyExists("userX")).thenReturn(false);

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, mockScanner);
        String result = service.checkUsernameAvailability();

        assertEquals("userX", result);
    }

    @Test
    void testReadPasswords_matching() {
        String input = "pass1\npass1\n";
        mockScanner = new Scanner(input);

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, mockScanner);
        String result = service.readPasswords();

        assertEquals("pass1", result);
    }

    @Test
    void testReadPasswords_mismatchThenMatch() {
        String input = "wrong\nnope\nright\nright\n";
        mockScanner = new Scanner(input);

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, mockScanner);
        String result = service.readPasswords();

        assertEquals("right", result);
    }
}