package passwordmanager;

import passwordmanager.database.Database;

import java.sql.SQLException;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Database.init();
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the password manager");
        System.out.println("*******************************");
        System.out.println("Please enter your username");
        String name = sc.next();
        System.out.println("Please enter your password");
        String password = sc.next();
    }

}