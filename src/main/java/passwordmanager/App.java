package passwordmanager;

import passwordmanager.database.Database;
import passwordmanager.security.SecurityUtil;
import passwordmanager.utility.MenuOption;

import java.util.Scanner;

public class App {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Database.init();
        String initialResponse = initialDialogue();
        MenuOption option = MenuOption.fromInput(initialResponse);
        switch (option){
            case LOGIN -> login();
            case REGISTER -> registerUser();
            case EXIT -> System.out.println("Exiting....");

        }
        sc.close();
    }

    private static String initialDialogue(){
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

    private static void login() {
    }

    private static void registerUser() {
        System.out.println(" ");
        System.out.println("Please enter a username:");
        String username = sc.next();  // Add check - username needs to be unique
        String password = readPasswords();
        SecurityUtil passwordUtility = new SecurityUtil();
        byte[] salt = SecurityUtil.generateSalt();
        Database.saveSalt(username, salt);
        //SecurityUtil.generateKey(password, salt);
    }

    private static String readPasswords() {
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