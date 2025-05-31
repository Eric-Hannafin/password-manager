package passwordmanager.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.database.DatabaseService;
import passwordmanager.exception.DeriveUserKeyException;
import passwordmanager.exception.SecretKeyFactoryException;
import passwordmanager.exception.ValidateUsernameException;
import passwordmanager.security.CryptoService;
import passwordmanager.security.SecurityService;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Scanner;

import static passwordmanager.utility.ConsoleUtil.clearConsole;

public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    private final DatabaseService databaseService;
    private final SecurityService securityService;
    private final CryptoService cryptoService;
    private final Scanner scanner;

    private static final String INITIAL_SITE_VALUE = "INITIAL_SITE";
    private static final String HEADER_BORDER = "************************************";

    public AuthenticationService() throws SecretKeyFactoryException, SQLException {
        this(new DatabaseService(), new SecurityService(), new CryptoService(), new Scanner(System.in));
    }

    public AuthenticationService(DatabaseService databaseService, SecurityService securityService, CryptoService cryptoService, Scanner scanner) {
        this.databaseService = databaseService;
        this.securityService = securityService;
        this.cryptoService = cryptoService;
        this.scanner = scanner;
    }

    public String initialDialogue(){
        System.out.println(HEADER_BORDER);
        System.out.println("***Welcome to the PasswordManager***");
        System.out.println(HEADER_BORDER);
        System.out.println(" ");
        System.out.println("Please select from the below options:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");

        return scanner.next();
    }

    public String registeredUserDialogue(){
        System.out.println(HEADER_BORDER);
        System.out.println("***PasswordManager***");
        System.out.println(HEADER_BORDER);
        System.out.println(" ");
        System.out.println("Please select from the below options:");
        System.out.println("1. Add new site");
        System.out.println("1. Retrieve password");
        System.out.println("3. Exit");
        return scanner.next();
    }

    public void login() {
        // TODO
    }

    public void registerUser() {
        System.out.println(" ");
        String username = checkUsernameAvailability();
        readPasswords();
        String salt = SecurityService.generateSalt();
        databaseService.saveSalt(username, salt);
        createInitialValue(username, salt);
        clearConsole();
    }

    protected void createInitialValue(String username, String salt) {
        try {
            SecretKey derivedUserKey = securityService.deriveUserKey(username, salt);
            String encryptedValue = cryptoService.encrypt(INITIAL_SITE_VALUE, derivedUserKey);
            databaseService.saveUserValue(INITIAL_SITE_VALUE, username, encryptedValue);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("No such algorithm", e);
        } catch (DeriveUserKeyException e) {
            LOGGER.error("Failed to derive the users key", e);
        } catch (Exception e) {
            LOGGER.error("An unexpected error during the encryption process", e);
        }
    }

    protected String checkUsernameAvailability() {
        while (true){
            System.out.println("Please enter a username:");
            String username = scanner.next();
            boolean exists = false;
            try {
                exists = databaseService.checkIfUserAlreadyExists(username);
            } catch (ValidateUsernameException e) {
                LOGGER.error("Failed to validate username", e);
            }
            if(!exists){
                return username;
            }
            System.out.println("Username is not available! Please try again.");
        }
    }

    protected String readPasswords() {
        String password1;
        String password2;

        while (true) {
            System.out.println("Please enter a password:");
            password1 = scanner.next();

            System.out.println("Please confirm your password:");
            password2 = scanner.next();

            if (password1.equals(password2)) {
                break;
            } else {
                System.out.println("Passwords do not match. Try again.");
            }
        }
        System.out.println("Password accepted.");
        return password1;
    }
}