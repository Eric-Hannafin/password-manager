package passwordmanager.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.database.DatabaseUtil;
import passwordmanager.security.SecurityUtil;
import static passwordmanager.utility.ConsoleUtil.clearConsole;

import java.util.Scanner;

public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    private static final Scanner sc = new Scanner(System.in);
    private static final String HEADER_BORDER = "************************************";

    public String initialDialogue(){
        LOGGER.info(HEADER_BORDER);
        LOGGER.info("***Welcome to the PasswordManager***");
        LOGGER.info(HEADER_BORDER);
        LOGGER.info(" ");
        LOGGER.info("Please select from the below options:");
        LOGGER.info("1. Login");
        LOGGER.info("2. Register");
        LOGGER.info("3. Exit");

        return sc.next();
    }

    public String registeredUserDialogue(){
        LOGGER.info(HEADER_BORDER);
        LOGGER.info("***Welcome to the PasswordManager***");
        LOGGER.info(HEADER_BORDER);
        LOGGER.info(" ");
        LOGGER.info("Please select from the below options:");
        LOGGER.info("1. Login");
        LOGGER.info("3. Exit");

        return sc.next();
    }

    public void login() {
        // TODO
    }

    public void registerUser() {
        LOGGER.info(" ");
        String username = checkUsernameAvailability();
        readPasswords();
        String salt = SecurityUtil.generateSalt();
        DatabaseUtil.saveSalt(username, salt);
        clearConsole();
        registeredUserDialogue();
    }

    private String checkUsernameAvailability(){
        while (true){
            LOGGER.info("Please enter a username:");
            String username = sc.next();
            boolean exists = DatabaseUtil.validateUsername(username);
            if(!exists){
                return username;
            }
            LOGGER.info("Username is not available! Please try again.");
        }
    }

    private String readPasswords() {
        String password1;
        String password2;

        while (true) {
            LOGGER.info("Please enter a password:");
            password1 = sc.next();

            LOGGER.info("Please confirm your password:");
            password2 = sc.next();

            if (password1.equals(password2)) {
                break;
            } else {
                LOGGER.info("Passwords do not match. Try again.");
            }
        }
        LOGGER.info("Password accepted.");
        return password1;
    }
}