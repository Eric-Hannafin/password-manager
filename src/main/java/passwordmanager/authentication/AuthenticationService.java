package passwordmanager.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.database.DatabaseService;
import passwordmanager.exception.SecretKeyFactoryException;
import passwordmanager.exception.ValidateUsernameException;
import passwordmanager.security.CryptoService;
import passwordmanager.security.SecurityService;
import passwordmanager.security.UserSession;
import passwordmanager.utility.ConsoleUtilImpl;

import javax.crypto.SecretKey;
import javax.security.sasl.AuthenticationException;
import java.io.Console;
import java.sql.SQLException;
import java.util.Scanner;

public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    private static final ConsoleUtilImpl consoleUtil = new ConsoleUtilImpl();
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
        consoleUtil.printLine(HEADER_BORDER);
        consoleUtil.printLine("**        PasswordManager         **");
        consoleUtil.printLine(HEADER_BORDER);
        consoleUtil.printLine(" ");
        consoleUtil.printLine("Please select from the below options:");
        consoleUtil.printLine("1. Login");
        consoleUtil.printLine("2. Register");
        consoleUtil.printLine("3. Exit");
        consoleUtil.printLine(" ");
        return scanner.next();
    }

    public String registeredUserDialogue(){
        consoleUtil.printLine(HEADER_BORDER);
        consoleUtil.printLine("**        PasswordManager         **");
        consoleUtil.printLine(HEADER_BORDER);
        consoleUtil.printLine(" ");
        consoleUtil.printLine("Please select from the below options:");
        consoleUtil.printLine("1. Add new site");
        consoleUtil.printLine("2. Retrieve a password");
        consoleUtil.printLine("3. Update a password");
        consoleUtil.printLine("4. List current sites");
        consoleUtil.printLine("5. Delete a site");
        consoleUtil.printLine("6. Exit");
        consoleUtil.printLine(" ");
        return scanner.next();
    }

    public UserSession login() {
        consoleUtil.printLine("\nPlease enter your username:");
        String username = scanner.next();

        consoleUtil.printLine("\nPlease enter your password:");
        String password;
        if (console != null) {
            char[] chars = console.readPassword();
            if (chars == null) {
                consoleUtil.printLine("No password entered.");
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

            consoleUtil.printLine("Login successful!");
            return new UserSession(username, userKey);

        } catch (AuthenticationException e) {
            consoleUtil.printLine("Invalid username or password.");
            LOGGER.warn("Login failed for '{}': {}", username, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected login error for '{}': {}", username, e.getMessage());
        }
        return null;
    }


    public void registerUser() {
        consoleUtil.printLine(" ");
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
            consoleUtil.printLine("Please enter a username:");
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
            consoleUtil.printLine("Username is not available! Please try again.");
        }
    }

    protected String readPasswords() {
        String password1;
        String password2;

        while (true) {
            consoleUtil.printLine("Please enter a password:");
            password1 = scanner.next();

            consoleUtil.printLine("Please confirm your password:");
            password2 = scanner.next();

            if (password1.equals(password2)) {
                break;
            } else {
                consoleUtil.printLine("Passwords do not match. Try again.");
            }
        }
        consoleUtil.printLine("Password accepted.");
        return password1;
    }
}