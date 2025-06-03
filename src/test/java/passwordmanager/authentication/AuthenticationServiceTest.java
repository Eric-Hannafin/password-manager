package passwordmanager.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passwordmanager.database.DatabaseService;
import passwordmanager.exception.DeriveUserKeyException;
import passwordmanager.exception.ValidateUsernameException;
import passwordmanager.security.CryptoService;
import passwordmanager.security.SecurityService;

import javax.crypto.SecretKey;
import java.io.Console;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    private DatabaseService mockDb;
    private SecurityService mockSec;
    private CryptoService mockCrypto;
    private Scanner mockScanner;
    private Console mockConsole;
    private AuthenticationService service;

    @BeforeEach
    void setup() {
        mockDb = mock(DatabaseService.class);
        mockSec = mock(SecurityService.class);
        mockCrypto = mock(CryptoService.class);
        mockConsole = mock(Console.class);
    }

    @Test
    void testCreateInitialValue_encryptionFlow() throws Exception {
        SecretKey mockKey = mock(SecretKey.class);
        when(mockSec.deriveUserKey("pass1", "salt1")).thenReturn(mockKey);
        when(mockCrypto.encrypt("INITIAL_SITE", mockKey)).thenReturn("encrypted");

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, new Scanner(System.in), mockConsole);
        service.createInitialValue("user1", "pass1", "salt1");

        verify(mockDb).saveUserValue("INITIAL_SITE", "user1", "encrypted");
    }

    @Test
    void testCreateInitialValue_handlesException() throws Exception {
        when(mockSec.deriveUserKey(any(), any())).thenThrow(new DeriveUserKeyException("fail", new Exception("fail")));

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, new Scanner(System.in), mockConsole);
        service.createInitialValue("test", "pass", "salt");

        // Expect no exception thrown
    }

    @Test
    void testCheckUsernameAvailability_acceptsValidUsername() throws ValidateUsernameException {
        String input = "user123\n";
        mockScanner = new Scanner(input);
        when(mockDb.checkIfUserAlreadyExists("user123")).thenReturn(false);

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, mockScanner, mockConsole);
        String result = service.checkUsernameAvailability();

        assertEquals("user123", result);
    }

    @Test
    void testCheckUsernameAvailability_rejectsAndRetries() throws ValidateUsernameException {
        String input = "taken\nuserX\n";
        mockScanner = new Scanner(input);
        when(mockDb.checkIfUserAlreadyExists("taken")).thenReturn(true);
        when(mockDb.checkIfUserAlreadyExists("userX")).thenReturn(false);

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, mockScanner, mockConsole);
        String result = service.checkUsernameAvailability();

        assertEquals("userX", result);
    }

    @Test
    void testReadPasswords_matching() {
        String input = "pass1\npass1\n";
        mockScanner = new Scanner(input);

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, mockScanner, mockConsole);
        String result = service.readPasswords();

        assertEquals("pass1", result);
    }

    @Test
    void testReadPasswords_mismatchThenMatch() {
        String input = "wrong\nnope\nright\nright\n";
        mockScanner = new Scanner(input);

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, mockScanner, mockConsole);
        String result = service.readPasswords();

        assertEquals("right", result);
    }

    @Test
    void testInitialDialogue_returnsUserChoice() {
        Scanner input = new Scanner("2\n");
        service = new AuthenticationService(mockDb, mockSec, mockCrypto, input, mockConsole);
        String choice = service.initialDialogue();
        assertEquals("2", choice);
    }

    @Test
    void testRegisteredUserDialogue_returnsUserChoice() {
        Scanner input = new Scanner("1\n");
        service = new AuthenticationService(mockDb, mockSec, mockCrypto, input, mockConsole);
        String choice = service.registeredUserDialogue();
        assertEquals("1", choice);
    }

    @Test
    void testRegisterUser_flowExecution() throws Exception {
        String input = "newUser\nmyPass\nmyPass\n";
        Scanner scanner = new Scanner(input);

        when(mockDb.checkIfUserAlreadyExists("newUser")).thenReturn(false);
        when(mockSec.deriveUserKey(eq("newUser"), anyString())).thenReturn(mock(SecretKey.class));
        when(mockCrypto.encrypt(anyString(), any())).thenReturn("encryptedVal");

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, scanner, mockConsole);
        service.registerUser();

        verify(mockDb).saveSalt(eq("newUser"), anyString());
        verify(mockDb).saveUserValue(eq("INITIAL_SITE"), eq("newUser"), eq("encryptedVal"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        Scanner scanner = new Scanner("testuser\n");
        Console mockConsole = mock(Console.class);
        when(mockConsole.readPassword()).thenReturn("testpass".toCharArray());

        SecretKey mockKey = mock(SecretKey.class);
        when(mockDb.getUserSalt("testuser")).thenReturn("somesalt");
        when(mockSec.deriveUserKey("testpass", "somesalt")).thenReturn(mockKey);
        when(mockDb.getUserInitialValue("testuser")).thenReturn("encryptedValue");
        when(mockCrypto.decrypt("encryptedValue", mockKey)).thenReturn("INITIAL_SITE");

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, scanner, mockConsole);
        service.login();

        verify(mockDb).getUserSalt("testuser");
        verify(mockDb).getUserInitialValue("testuser");
        verify(mockCrypto).decrypt("encryptedValue", mockKey);
    }


    @Test
    void testLoginFailure() throws Exception {
        Scanner scanner = new Scanner("testuser\n");
        Console mockConsole = mock(Console.class);
        when(mockConsole.readPassword()).thenReturn("testpass".toCharArray());

        SecretKey mockKey = mock(SecretKey.class);
        when(mockDb.getUserSalt("testuser")).thenReturn("somesalt");
        when(mockSec.deriveUserKey("testpass", "somesalt")).thenReturn(mockKey);
        when(mockDb.getUserInitialValue("testuser")).thenReturn("encryptedValue");
        when(mockCrypto.decrypt("encryptedValue", mockKey)).thenReturn("WRONG_VALUE");

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, scanner, mockConsole);
        service.login();

        verify(mockDb).getUserSalt("testuser");
        verify(mockDb).getUserInitialValue("testuser");
        verify(mockCrypto).decrypt("encryptedValue", mockKey);
    }


    @Test
    void testLoginThrowsException() throws Exception {
        Scanner scanner = new Scanner("testuser\n");
        Console mockConsole = mock(Console.class);
        when(mockConsole.readPassword()).thenReturn("testpass".toCharArray());

        when(mockDb.getUserSalt("testuser")).thenThrow(new RuntimeException("DB error"));

        service = new AuthenticationService(mockDb, mockSec, mockCrypto, scanner, mockConsole);
        service.login();

        verify(mockDb).getUserSalt("testuser");
    }
}