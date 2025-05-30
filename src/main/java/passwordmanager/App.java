package passwordmanager;

import passwordmanager.authentication.AuthenticationService;
import passwordmanager.database.DatabaseUtil;
import passwordmanager.utility.MenuOption;

public class App {

    private AuthenticationService authenticationService = new AuthenticationService();

    public void run() {
        DatabaseUtil.init();
        String initialResponse = authenticationService.initialDialogue();
        MenuOption option = MenuOption.fromInput(initialResponse);
        switch (option) {
            case LOGIN -> authenticationService.login();
            case REGISTER -> authenticationService.registerUser();
            case EXIT -> System.out.println("Exiting....");
        }
    }

    public static void main(String[] args) {
        new App().run();
    }
}