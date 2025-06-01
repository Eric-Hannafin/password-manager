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
import java.sql.SQLException;
import java.util.Scanner;

public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    private static final ConsoleUtil consoleUtil = new ConsoleUtil();
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
        System.out.println("** Welcome to the PasswordManager **");
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
        System.out.println("3. List current sites");
        System.out.println("4. Delete a site");
        System.out.println("5. Exit");
        return scanner.next();
    }

    public UserSession login() {
        System.out.println("Please enter your username:");
        String username = scanner.next();

        System.out.println("Please enter your password:");
        String password = scanner.next();
        try {
            String salt = databaseService.getUserSalt(username);
            SecretKey userKey = securityService.deriveUserKey(password, salt);

            String encryptedInitial = databaseService.getUserInitialValue(username);
            String decrypted = cryptoService.decrypt(encryptedInitial, userKey);

            if (INITIAL_SITE_VALUE.equals(decrypted)) {
                System.out.println("Login successful!");
                return new UserSession(username, userKey);
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (Exception e) {
            System.out.println("Login failed: Invalid username or password.");
            LOGGER.error("Login error for user {}: {}", username, e.getMessage());
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