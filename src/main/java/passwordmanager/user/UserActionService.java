package passwordmanager.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.database.DatabaseService;
import passwordmanager.security.CryptoService;
import passwordmanager.security.UserSession;
import passwordmanager.utility.ConsoleUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class UserActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserActionService.class);

    private final DatabaseService databaseService;
    private final ConsoleUtil consoleUtil;
    private final CryptoService cryptoService;
    private final Scanner scanner;


    public UserActionService(Scanner scanner, DatabaseService databaseService, CryptoService cryptoService, ConsoleUtil consoleUtil) {
        this.databaseService = databaseService;
        this.cryptoService = cryptoService;
        this.scanner = scanner;
        this.consoleUtil = consoleUtil;
    }

    public UserActionService(ConsoleUtil consoleUtil) throws SQLException {
        this(new Scanner(System.in), new DatabaseService(), new CryptoService(), consoleUtil);
    }


    public void addValue(UserSession userSession) {
        while (true) {
            System.out.print("Please enter site name: ");
            String site = scanner.nextLine().trim();

            if (site.isBlank()) {
                consoleUtil.printLine("Site name must not be empty.");
                continue;
            }
            boolean exists = databaseService.checkIfSiteAlreadyExists(site, userSession);
            if (exists) {
                consoleUtil.printLine("A value for that site already exists. Please choose another.");
                consoleUtil.printLine(" ");
                continue;
            }
            System.out.print("Please enter password for site: ");
            String password = scanner.nextLine().trim();
            if (password.isBlank()) {
                consoleUtil.printLine("Password must not be empty.");
                continue;
            }
            try {
                String encryptedPassword = cryptoService.encrypt(password, userSession.userKey());
                databaseService.saveUserValue(site, userSession.username(), encryptedPassword);
                consoleUtil.printLine("Password saved successfully. Press enter to return to the main menu");
                new Scanner(System.in).nextLine();
                return;
            } catch (Exception e) {
                consoleUtil.printLine("Failed to encrypt and save the password.");
                LOGGER.error("Encryption failed for site '{}': {}", site, e.getMessage(), e);
                return;
            }
        }
    }


    public String getUserPassword(UserSession userSession){
        consoleUtil.printLine(" ");
        consoleUtil.printLine("Please enter site name: ");
        String site = scanner.next();

        if (site == null || site.isBlank()) {
            consoleUtil.printLine("Site name must not be empty.");
        }
        try {
            String userSitePassword = databaseService.getUserSitePassword(site);
            return cryptoService.decrypt(userSitePassword, userSession.userKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void listAllUserSites(UserSession userSession) {
        try {
            List<String> sites = databaseService.listAllUserSites(userSession);
            int i = 1;
            for (String site : sites) {
                if(i == 1){
                    consoleUtil.printLine("");
                }
                consoleUtil.printLine(i + ": " + site);
                i++;
            }
            consoleUtil.printLine("Press Enter to return to the menu...");
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve user sites", e);
        }
    }
}