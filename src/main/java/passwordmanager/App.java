package passwordmanager;

import passwordmanager.database.Database;

import java.util.Scanner;

public class App {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Database.init();
        String initialResponse = initialDialogue();
    }

    private static String initialDialogue(){
        System.out.println("*******************************");
        System.out.println("Welcome to the password manager");
        System.out.println("*******************************");
        System.out.println(" ");
        System.out.println("Please select from the below options:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");

        return sc.next();
    }

}