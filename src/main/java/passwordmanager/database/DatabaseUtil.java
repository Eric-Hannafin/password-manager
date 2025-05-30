package passwordmanager.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Scanner;

public class DatabaseUtil {

    private static final String DB_URL = "jdbc:sqlite:passwords.db";

    public static void init() {
        try(Connection connection = DriverManager.getConnection(DB_URL)){
            System.out.println("Successfully connected to database");
            Statement statement = connection.createStatement();
            String passwords = loadSqlFromResource("sql/init.sql");
            String salts = loadSqlFromResource("sql/salts.sql");
            statement.executeUpdate(passwords);
            statement.executeUpdate(salts);
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Failed to establish database connection");
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static String loadSqlFromResource(String filename) {
        try (InputStream in = DatabaseUtil.class.getClassLoader().getResourceAsStream(filename);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SQL from file: " + filename, e);
        }
    }

    public static void saveSalt(String username, String salt){
        String insertSQL = "INSERT INTO salts (username, salt) VALUES (?, ?)";
        try(Connection connection = connect(); PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, salt);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean validateUsername(String username) {
        String sql = "SELECT * FROM salts WHERE username = ?";
        try {
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("An unexpected error occurred while trying to validate the username");
        }
    }
}