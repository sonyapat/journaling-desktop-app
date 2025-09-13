import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class creates the Front Page with login and account creation for the personal journal application.
 * It handles user registration, authentication, and opening the journal UI.
 *
 * @author Sonya Pathania
 * @version 2.0
 */
public class FrontPage extends Application {

    /** Encryptor object used for password hashing and verification */
    private PWEncryptor encryptor;

    /** SQLite database URL for storing user credentials */
    private static final String DB_URL = "jdbc:sqlite:users.db";

    /**
     * Main entry point for the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application by creating the login UI, setting up event handlers,
     * and checking for existing users.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        encryptor = new PWEncryptor();
        createUsersTableIfNotExists();

        Label title = new Label("Welcome to Your Journal!");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Create Account");

        Label feedback = new Label();

        // Event handler for login button
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (authenticateUser(username, password)) {
                feedback.setText("Login successful!");
                openJournal(primaryStage);
            } else {
                feedback.setText("Invalid username or password!");
            }
        });

        // Disable account creation if a user already exists
        if (anyUserExists()) {
            registerBtn.setDisable(true);
        }

        // Event handler for register button
        registerBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                feedback.setText("Username and password cannot be empty!");
            } else if (registerUser(username, password)) {
                feedback.setText("Account created! Please login.");
                registerBtn.setDisable(true);
            } else {
                feedback.setText("Username already exists!");
            }
        });

        VBox layout = new VBox(10, title, usernameField, passwordField, loginBtn, registerBtn, feedback);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        primaryStage.setScene(new Scene(layout, 400, 400));
        primaryStage.setTitle("Journal Login");
        primaryStage.show();
    }

    /** Checks if any user exists in the database. */
    private boolean anyUserExists() {
        String sql = "SELECT username FROM users LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next();
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
            return false;
        }
    }

    /** Creates the 'users' table in the database if it does not already exist. */
    private void createUsersTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY," +
                "password TEXT NOT NULL)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating users table: " + e.getMessage());
        }
    }

    /** Registers a new user with the given username and password. */
    private boolean registerUser(String username, String password) {
        if (isUserExists(username)) return false;
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, encryptor.encode(password, 3));
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
            return false;
        }
    }

    /** Checks if a username already exists in the database. */
    private boolean isUserExists(String username) {
        String sql = "SELECT username FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
            return false;
        }
    }

    /** Authenticates a user by verifying the password against the stored hash. */
    private boolean authenticateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String stored = rs.getString("password");
                return encryptor.checkPassword(password, stored);
            }
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
        return false;
    }

    /** Opens the main journal UI after a successful login. */
    private void openJournal(Stage primaryStage) {
        Journal journal = new Journal();
        Scene journalScene = new Scene(journal.getPage(), 600, 500);
        primaryStage.setScene(journalScene);
        primaryStage.setTitle("Journal");
    }
}