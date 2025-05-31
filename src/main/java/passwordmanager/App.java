package passwordmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.authentication.AuthenticationService;
import passwordmanager.database.DatabaseService;
import passwordmanager.utility.MenuOptionEnum;

import static passwordmanager.utility.ConsoleUtil.clearConsole;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private AuthenticationService authenticationService = new AuthenticationService();
    private DatabaseService databaseService = new DatabaseService();

    public void run() {
        databaseService.init();
        LOGGER.info("Database initialized");
        while (true) {
            String initialResponse = authenticationService.initialDialogue();
            MenuOptionEnum option = MenuOptionEnum.fromInput(initialResponse);
            switch (option) {
                case LOGIN -> {
                    authenticationService.login();
                    clearConsole();
                    showLoggedInMenu();
                }
                case REGISTER -> authenticationService.registerUser();
                case EXIT -> {
                    LOGGER.info("Exiting....");
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        new App().run();
    }

    private void showLoggedInMenu() {
        while (true) {
            String menuSelection = authenticationService.registeredUserDialogue();
            System.out.println("HERE");
            System.exit(1);
        }
    }
}