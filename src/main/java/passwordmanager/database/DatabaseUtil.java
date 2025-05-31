package passwordmanager.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.exception.FailedToLoadSQLResourceException;
import passwordmanager.exception.ValidateUsernameException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Scanner;

public class DatabaseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtil.class);
    private static final String DB_URL = "jdbc:sqlite:passwords.db";

    private DatabaseUtil(){}

    public static void init() {
        try(Connection connection = connect();
            Statement statement = connection.createStatement()){
            LOGGER.info("Successfully connected to database");
            String passwords = loadSqlFromResource("sql/init.sql");
            String salts = loadSqlFromResource("sql/salts.sql");
            statement.executeUpdate(passwords);
            statement.executeUpdate(salts);
        } catch (SQLException | FailedToLoadSQLResourceException e){
            LOGGER.error("Failed to establish database connection", e);
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static String loadSqlFromResource(String filename) throws FailedToLoadSQLResourceException {
        try (InputStream in = DatabaseUtil.class.getClassLoader().getResourceAsStream(filename);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new FailedToLoadSQLResourceException("Failed to load SQL from file: " + filename, e);
        }
    }

    public static void saveSalt(String username, String salt){
        String sql = "INSERT INTO salts (username, salt) VALUES (?, ?)";
        try(Connection connection = connect(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, salt);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserValue(String username, String site, String password){
            String sql = "INSERT INTO passwords (site, username, password) VALUES (?, ?, ?)";
        try(Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, site);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            LOGGER.info("Saving user value");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("An error occurred when trying to save the users value", e);
        }
    }

    public static boolean checkIfUserAlreadyExists(String username) throws ValidateUsernameException {
        String sql = "SELECT salts.username FROM salts WHERE username = ?";
        try(Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e){
            throw new ValidateUsernameException("An unexpected error occurred while trying to validate the username", e);
        }
    }
}