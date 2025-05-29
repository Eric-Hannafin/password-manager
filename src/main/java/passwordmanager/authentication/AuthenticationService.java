package passwordmanager.authentication;

import passwordmanager.database.Database;
import passwordmanager.security.SecurityUtil;

import java.sql.SQLException;
import java.util.Scanner;

public class AuthenticationService {

    private static final Scanner sc = new Scanner(System.in);

    public String initialDialogue(){
        System.out.println("************************************");
        System.out.println("***Welcome to the PasswordManager***");
        System.out.println("************************************");
        System.out.println(" ");
        System.out.println("Please select from the below options:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");

        return sc.next();
    }

    public void login() {
    }

    public void registerUser() {
        System.out.println(" ");
        String username = checkUsernameAvailability();
        String password = readPasswords();
        SecurityUtil passwordUtility = new SecurityUtil();
        byte[] salt = SecurityUtil.generateSalt();
        Database.saveSalt(username, salt);
    }

    private String checkUsernameAvailability(){
        while (true){
            System.out.println("Please enter a username:");
            String username = sc.next();
            boolean exists = Database.validateUsername(username);
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