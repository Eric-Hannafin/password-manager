package passwordmanager;

import passwordmanager.authentication.AuthenticationService;
import passwordmanager.database.Database;
import passwordmanager.utility.MenuOption;

public class App {

    public static void main(String[] args) {
        AuthenticationService authenticationService = new AuthenticationService();
        Database.init();
        String initialResponse = authenticationService.initialDialogue();
        MenuOption option = MenuOption.fromInput(initialResponse);
        switch (option){
            case LOGIN -> authenticationService.login();
            case REGISTER -> authenticationService.registerUser();
            case EXIT -> System.out.println("Exiting....");

        }
    }
}