package passwordmanager.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:passwords.db";

    public static void init() {
        System.out.println("Working directory: " + System.getProperty("user.dir"));
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
        try (InputStream in = Database.class.getClassLoader().getResourceAsStream(filename);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SQL from file: " + filename, e);
        }
    }

}