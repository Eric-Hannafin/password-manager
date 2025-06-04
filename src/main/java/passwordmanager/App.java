package passwordmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.authentication.AuthenticationService;
import passwordmanager.database.DatabaseService;
import passwordmanager.security.UserSession;
import passwordmanager.user.UserActionService;
import passwordmanager.utility.ConsoleUtil;
import passwordmanager.utility.LoggedInMenuOptionEnum;
import passwordmanager.utility.MenuOptionEnum;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private final AuthenticationService authenticationService;
    private final UserActionService userActionService;
    private final DatabaseService databaseService;
    private final ConsoleUtil consoleUtil;

    public App(AuthenticationService authService, DatabaseService dbService, ConsoleUtil consoleUtil, UserActionService userActionService) {
        this.authenticationService = authService;
        this.databaseService = dbService;
        this.consoleUtil = consoleUtil;
        this.userActionService = userActionService;
    }

    public void run() {
        databaseService.init();
        LOGGER.info("Database initialized");

        while (true) {
            consoleUtil.clearConsole();
            String input = authenticationService.initialDialogue();
            MenuOptionEnum option = MenuOptionEnum.fromInput(input);

            switch (Objects.requireNonNull(option)) {
                case LOGIN -> {
                    UserSession userSession = authenticationService.login();
                    if(null != userSession){
                        consoleUtil.clearConsole();
                        showLoggedInMenu(userSession);
                    } else {
                        System.out.println("Login Failed. Please try again");
                        System.out.println("Press enter to return and try again");
                        new Scanner(System.in).nextLine();
                    }
                }
                case REGISTER -> authenticationService.registerUser();
                case EXIT -> {
                    LOGGER.info("Exiting...");
                    return;
                }
                default -> throw new IllegalArgumentException("Invalid input: " + input);
            }
        }
    }

    private void showLoggedInMenu(UserSession userSession) {

        while (true) {
            consoleUtil.clearConsole();
            String input = authenticationService.registeredUserDialogue();
            LoggedInMenuOptionEnum option = LoggedInMenuOptionEnum.fromInput(input);
            System.out.println(option);
            switch (Objects.requireNonNull(option)) {
                case ADD -> {
                    consoleUtil.clearConsole();
                    userActionService.addValue(userSession);
                }
                case RETRIEVE -> {
                    String sitePassword = userActionService.getUserPassword(userSession);
                    consoleUtil.clearConsole();
                    System.out.println("Your password is: " + sitePassword);
                    System.out.print("Press Enter to return to the menu...");
                    new Scanner(System.in).nextLine();
                }
                case UPDATE -> System.out.println();
                case LIST -> {
                    userActionService.listAllUserSites(userSession);
                    new Scanner(System.in).nextLine();
                }
                case DELETE -> System.out.println("HERE");
                case EXIT -> {
                    consoleUtil.clearConsole();
                    return;
                }
                default -> throw new IllegalArgumentException("Invalid input " + input);
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        App app = new App(new AuthenticationService(), new DatabaseService(), new ConsoleUtil(), new UserActionService());
        app.run();
    }
}