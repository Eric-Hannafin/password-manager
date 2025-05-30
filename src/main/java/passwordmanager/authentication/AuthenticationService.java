package passwordmanager.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.database.DatabaseUtil;
import passwordmanager.exeption.ValidateUsernameException;
import passwordmanager.security.SecurityUtil;

import static passwordmanager.utility.ConsoleUtil.clearConsole;
import static passwordmanager.database.DatabaseUtil.checkIfUserAlreadyExists;

import java.util.Scanner;

public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    private static final Scanner sc = new Scanner(System.in);
    private static final String HEADER_BORDER = "************************************";
    private boolean isUserRegistered = false;

    public String initialDialogue(){
        System.out.println(HEADER_BORDER);
        System.out.println("***Welcome to the PasswordManager***");
        System.out.println(HEADER_BORDER);
        System.out.println(" ");
        System.out.println("Please select from the below options:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");

        return sc.next();
    }

    public String registeredUserDialogue(){
        System.out.println(HEADER_BORDER);
        System.out.println("***TEST OPTIONS");
        System.out.println(HEADER_BORDER);
        System.out.println(" ");
        System.out.println("Please select from the below options:");
        System.out.println("1. ADD NEW SITE");
        System.out.println("3. Exit");
        return sc.next();
    }

    public void login() {
        // TODO
    }

    public void registerUser() {
        System.out.println(" ");
        String username = checkUsernameAvailability();
        readPasswords();
        String salt = SecurityUtil.generateSalt();
        DatabaseUtil.saveSalt(username, salt);
        clearConsole();
        isUserRegistered = true;
    }

    private String checkUsernameAvailability() {
        while (true){
            System.out.println("Please enter a username:");
            String username = sc.next();
            boolean exists = false;
            try {
                exists = DatabaseUtil.checkIfUserAlreadyExists(username);
            } catch (ValidateUsernameException e) {
                LOGGER.error("Failed to validate username", e);
            }
            if(!exists){
                return username;
            }
            System.out.println("Username is not available! Please try again.");
        }
    }

    private String readPasswords() {
        String password1;
        String password2;

        while (true) {
            System.out.println("Please enter a password:");
            password1 = sc.next();

            System.out.println("Please confirm your password:");
            password2 = sc.next();

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