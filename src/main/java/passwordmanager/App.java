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

        boolean running = true;
        while (running) {
            String input = authenticationService.initialDialogue();
            MenuOptionEnum option = MenuOptionEnum.fromInput(input);

            switch (option) {
                case LOGIN -> {
                    authenticationService.login();
                    consoleUtil.clearConsole();
                    showLoggedInMenu(); // this can also return a flag if needed
                }
                case REGISTER -> authenticationService.registerUser();
                case EXIT -> {
                    LOGGER.info("Exiting...");
                    running = false;
                }
            }
        }
    }

    private void showLoggedInMenu() {
        boolean loggedIn = true;
        while (loggedIn) {
            String menuSelection = authenticationService.registeredUserDialogue();
            System.out.println("HERE");
            loggedIn = false;
        }
    }

    public static void main(String[] args) throws SQLException {
        App app = new App(new AuthenticationService(), new DatabaseService(), new ConsoleUtil());
        app.run();
    }
}