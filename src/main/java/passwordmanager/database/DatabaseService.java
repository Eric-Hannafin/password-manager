package passwordmanager.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.exception.FailedToLoadSQLResourceException;
import passwordmanager.exception.InitialUserValueException;
import passwordmanager.exception.ValidateUsernameException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Scanner;

public class DatabaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);
    private final Connection connection;

    public DatabaseService(Connection connection) {
        this.connection = connection;
    }

    public DatabaseService() throws SQLException {
        this(DriverManager.getConnection("jdbc:sqlite:passwords.db"));
    }

    public void init() {
        try (Statement statement = connection.createStatement()) {
            LOGGER.info("Successfully connected to database");
            String sqlCombined = loadSqlFromResource("sql/init.sql") + "\n" + loadSqlFromResource("sql/salts.sql");
            String[] statements = sqlCombined.split(";");

            for (String sql : statements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }

        } catch (SQLException | FailedToLoadSQLResourceException e) {
            LOGGER.error("Failed to establish database connection", e);
        }
    }

    public Connection connect() {
        return this.connection;
    }

    public String loadSqlFromResource(String filename) throws FailedToLoadSQLResourceException {
        try (InputStream in = DatabaseService.class.getClassLoader().getResourceAsStream(filename);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new FailedToLoadSQLResourceException("Failed to load SQL from file: " + filename, e);
        }
    }

    public void saveSalt(String username, String salt) {
        String sql = "INSERT INTO salts (username, salt) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, salt);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("An unexpected error occurred when saving the user salt", e);
        }
    }

    public String getUserSalt(String username){
        String sql = "SELECT salt from salts WHERE username = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getString("salt");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUserValue(String site, String username, String password) {
        String sql = "INSERT INTO passwords (site, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, site);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            LOGGER.info("Saving user value");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("An error occurred when trying to save the users value", e);
        }
    }

    public boolean checkIfUserAlreadyExists(String username) throws ValidateUsernameException {
        String sql = "SELECT salts.username FROM salts WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new ValidateUsernameException("An unexpected error occurred while trying to validate the username", e);
        }
    }

    public String getUserInitialValue(String username) {
        String sql = "SELECT password FROM passwords WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("password");
            } else {
                throw new InitialUserValueException("Failed to get initial value for user: " + username);
            }
        } catch (SQLException e) {
            throw new InitialUserValueException("Failed to get initial value for user: " + username, e);
        }
    }


}