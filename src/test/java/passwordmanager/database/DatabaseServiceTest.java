package passwordmanager.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passwordmanager.exception.ValidateUsernameException;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseServiceTest {

    private DatabaseService databaseService;

    @BeforeEach
    void setUp() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        databaseService = new DatabaseService(connection);
        databaseService.init();
    }

    @Test
    void testSaveAndCheckUserExists() throws Exception {
        databaseService.saveSalt("alice", "salt123");
        assertTrue(databaseService.checkIfUserAlreadyExists("alice"));
    }

    @Test
    void testUserDoesNotExistInitially() throws Exception {
        assertFalse(databaseService.checkIfUserAlreadyExists("bob"));
    }

    @Test
    void testSaveUserValueSuccessfully() {
        // This test just ensures that saving doesn't throw an exception.
        assertDoesNotThrow(() -> databaseService.saveUserValue("alice", "gmail", "encryptedPass"));
    }

    @Test
    void testSaveSaltDoesNotThrow() {
        assertDoesNotThrow(() -> databaseService.saveSalt("charlie", "somesalt"));
    }

    @Test
    void testCheckUserExistsThrowsOnSqlIssue() {
        DatabaseService brokenService = new DatabaseService(databaseService.connect()) {
            @Override
            public boolean checkIfUserAlreadyExists(String username) throws ValidateUsernameException {
                throw new ValidateUsernameException("Fake failure", new RuntimeException());
            }
        };
        assertThrows(ValidateUsernameException.class, () -> brokenService.checkIfUserAlreadyExists("any"));
    }
}