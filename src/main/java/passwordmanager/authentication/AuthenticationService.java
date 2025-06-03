package passwordmanager.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.database.DatabaseService;
import passwordmanager.exception.SecretKeyFactoryException;
import passwordmanager.exception.ValidateUsernameException;
import passwordmanager.security.CryptoService;
import passwordmanager.security.SecurityService;
import passwordmanager.security.UserSession;
import passwordmanager.utility.ConsoleUtil;

import javax.crypto.SecretKey;
import javax.security.sasl.AuthenticationException;
import java.io.Console;
import java.sql.SQLException;
import java.util.Scanner;

public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    private static final ConsoleUtil consoleUtil = new ConsoleUtil();
    private final DatabaseService databaseService;
    private final SecurityService securityService;
    private final CryptoService cryptoService;
    private final Console console;
    private final Scanner scanner;

    private static final String INITIAL_SITE_VALUE = "INITIAL_SITE";
    private static final String HEADER_BORDER = "************************************";

    public AuthenticationService() throws SecretKeyFactoryException, SQLException {
        this(new DatabaseService(), new SecurityService(), new CryptoService(), new Scanner(System.in), System.console());
    }

    public AuthenticationService(DatabaseService databaseService, SecurityService securityService, CryptoService cryptoService, Scanner scanner, Console console) {
        this.databaseService = databaseService;
        this.securityService = securityService;
        this.cryptoService = cryptoService;
        this.scanner = scanner;
        this.console = console;
    }

    public String initialDialogue(){
        System.out.println(HEADER_BORDER);
        System.out.println("***        PasswordManager        ***");
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
        System.out.println("***        PasswordManager        ***");
        System.out.println(HEADER_BORDER);
        System.out.println(" ");
        System.out.println("Please select from the below options:");
        System.out.println("1. Add new site");
        System.out.println("2. Retrieve a password");
        System.out.println("3. Update a password");
        System.out.println("4. List current sites");
        System.out.println("5. Delete a site");
        System.out.println("6. Exit");
        return scanner.next();
    }

    public UserSession login() {
        System.out.println("\nPlease enter your username:");
        String username = scanner.next();

        System.out.println("\nPlease enter your password:");
        String password;
        if (console != null) {
            char[] chars = console.readPassword();
            if (chars == null) {
                System.out.println("No password entered.");
                return null;
            }
            password = new String(chars);
        } else {
            System.out.print("(Input not masked) Password: ");
            password = scanner.next();
        }

        try {
            String salt = databaseService.getUserSalt(username);
            SecretKey userKey = securityService.deriveUserKey(password, salt);
            String encryptedInitial = databaseService.getUserInitialValue(username);
            String decrypted = cryptoService.decrypt(encryptedInitial, userKey);

            if (!INITIAL_SITE_VALUE.equals(decrypted)) {
                throw new AuthenticationException("Decryption did not match expected value.");
            }

            System.out.println("Login successful!");
            return new UserSession(username, userKey);

        } catch (AuthenticationException e) {
            System.out.println("Invalid username or password.");
            LOGGER.warn("Login failed for '{}': {}", username, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected login error for '{}': {}", username, e.getMessage());
        }
        return null;
    }


    public void registerUser() {
        System.out.println(" ");
        String username = checkUsernameAvailability();
        String password = readPasswords(); // ← capture the password here
        String salt = SecurityService.generateSalt();
        databaseService.saveSalt(username, salt);
        createInitialValue(username, password, salt); // ← pass password
        consoleUtil.clearConsole();
    }


    public void createInitialValue(String username, String password, String salt) {
        try {
            SecretKey derivedUserKey = securityService.deriveUserKey(password, salt);
            String encryptedValue = cryptoService.encrypt("INITIAL_SITE", derivedUserKey);
            databaseService.saveUserValue("INITIAL_SITE", username, encryptedValue);
        } catch (Exception e) {
            LOGGER.error("Failed during encryption", e);
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