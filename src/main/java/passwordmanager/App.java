package passwordmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.authentication.AuthenticationService;
import passwordmanager.database.DatabaseService;
import passwordmanager.utility.ConsoleUtil;
import passwordmanager.utility.MenuOptionEnum;

import java.sql.SQLException;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private final AuthenticationService authenticationService;
    private final DatabaseService databaseService;
    private final ConsoleUtil consoleUtil;

    public App(AuthenticationService authService, DatabaseService dbService, ConsoleUtil consoleUtil) {
        this.authenticationService = authService;
        this.databaseService = dbService;
        this.consoleUtil = consoleUtil;
    }

    public void run() {
        databaseService.init();
        LOGGER.info("Database initialized");

        while (true) {
            String input = authenticationService.initialDialogue();
            MenuOptionEnum option = MenuOptionEnum.fromInput(input);

            switch (option) {
                case LOGIN -> {
                    authenticationService.login();
                    consoleUtil.clearConsole();
                    showLoggedInMenu();
                }
                case REGISTER -> authenticationService.registerUser();
                case EXIT -> {
                    LOGGER.info("Exiting...");
                    return;
                }
            }
        }
    }

    private void showLoggedInMenu() {
            String menuSelection = authenticationService.registeredUserDialogue();
    }

    public static void main(String[] args) throws SQLException {
        App app = new App(new AuthenticationService(), new DatabaseService(), new ConsoleUtil());
        app.run();
    }
}