package passwordmanager.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.database.DatabaseService;
import passwordmanager.security.CryptoService;
import passwordmanager.security.UserSession;

import java.sql.SQLException;
import java.util.Scanner;

public class UserActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserActionService.class);

    private final DatabaseService databaseService;
    private final CryptoService cryptoService;
    private final Scanner scanner;


    public UserActionService(Scanner scanner, DatabaseService databaseService, CryptoService cryptoService) {
        this.databaseService = databaseService;
        this.cryptoService = cryptoService;
        this.scanner = scanner;
    }

    public UserActionService() throws SQLException {
        this(new Scanner(System.in), new DatabaseService(), new CryptoService());
    }


    public void addValue(UserSession userSession) {
        System.out.println("Please enter site name: ");
        String site = scanner.next();
        System.out.println("Please enter password for site: ");
        String password = scanner.next();

        if (site == null || site.isBlank() || password == null || password.isBlank()) {
            System.out.println("Site name and password must not be empty.");
            return;
        }

        try {
            String encryptedPassword = cryptoService.encrypt(password, userSession.userKey());
            databaseService.saveUserValue(site, userSession.username(), encryptedPassword);
            System.out.println("Password saved successfully.");
        } catch (Exception e) {
            System.out.println("Failed to encrypt and save the password.");
            LOGGER.error("Encryption failed for site {}: {}", site, e.getMessage());
        }
    }

}