import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDateTime;

/**
 * This class represents the main journal UI of the personal reflection application.
 * It handles displaying entries, creating new entries, and interacting with the database.
 * Entries are stored persistently in a local SQLite database.
 *
 * Author: Sonya Pathania
 * Version: 2.0
 */
public class Journal {

    /** VBox container holding all journal entries for display */
    private VBox eachEntry;

    /** List of all journal entries loaded from the database */
    private ArrayList<Entry> entries = new ArrayList<>();

    /** SQLite database URL for storing journal entries */
    private static final String DB_URL = "jdbc:sqlite:entries.db";

    /**
     * Constructs a Journal object.
     * Initializes the entry container, creates the database table if it does not exist,
     * loads existing entries from the database, and updates the display.
     */
    public Journal() {
        eachEntry = new VBox(10);
        eachEntry.setPadding(new Insets(10));
        createTableIfNotExists();
        loadEntriesFromDatabase();
        updateEntryDisplay();
    }

    /**
     * Returns the main page layout of the journal as a VBox.
     * Includes header, new entry button, and scrollable area of entries.
     *
     * @return VBox containing the full journal UI
     */
    public VBox getPage() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label header = new Label("My Journal");
        header.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; " +
                "-fx-background-color: #1aa780; -fx-text-fill: white; -fx-padding: 10px;");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        Button newEntryBtn = new Button("New Entry");
        newEntryBtn.setStyle("-fx-background-color: #1aa780; -fx-text-fill: white; -fx-font-weight: bold;");
        newEntryBtn.setOnAction(e -> showNewEntryWindow());

        HBox topBar = new HBox(newEntryBtn);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10, 0, 10, 0));

        ScrollPane scrollPane = new ScrollPane(eachEntry);
        scrollPane.setFitToWidth(true);

        layout.getChildren().addAll(header, topBar, scrollPane);
        return layout;
    }

    /**
     * Opens a new window to allow the user to create a new journal entry.
     * Saves the entry to the database and updates the display.
     */
    private void showNewEntryWindow() {
        Stage window = new Stage();
        window.setTitle("New Entry");

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Write your journal entry here...");
        bodyField.setWrapText(true);
        bodyField.setPrefRowCount(10);

        Button postButton = new Button("Post");
        postButton.setStyle("-fx-background-color: #1aa780; -fx-text-fill: white; -fx-font-weight: bold;");

        box.getChildren().addAll(new Label("Entry:"), bodyField, postButton);

        postButton.setOnAction(e -> {
            String body = bodyField.getText().trim();
            if (!body.isEmpty()) {
                LocalDateTime timestamp = LocalDateTime.now();
                int id = saveEntryToDatabase(body, timestamp);
                if (id != -1) { // ensure saving succeeded
                    entries.add(new Entry(id, body, timestamp));
                    window.close(); // close window immediately after successful save
                    updateEntryDisplay();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save entry.").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Entry cannot be empty").show();
            }
        });

        window.setScene(new Scene(box, 400, 300));
        window.show();
    }

    /**
     * Updates the display of journal entries in the main VBox.
     * Shows a placeholder message if no entries exist.
     */
    private void updateEntryDisplay() {
        eachEntry.getChildren().clear();
        if (entries.isEmpty()) {
            Label empty = new Label("Your journal is empty. Start writing!");
            empty.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
            empty.setAlignment(Pos.CENTER);
            empty.setMaxWidth(Double.MAX_VALUE);
            eachEntry.getChildren().add(empty);
        } else {
            for (int i = entries.size() - 1; i >= 0; i--) {
                eachEntry.getChildren().add(entries.get(i).getEntry());
            }
        }
    }

    /**
     * Creates the 'entries' table in the database if it does not already exist.
     */
    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS entries (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "body TEXT NOT NULL," +
                "timestamp TEXT NOT NULL)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    /**
     * Saves a new journal entry to the database.
     *
     * @param body the text content of the journal entry
     * @param timestamp the timestamp of the entry
     * @return the auto-generated ID of the new entry, or -1 if saving fails
     */
    private int saveEntryToDatabase(String body, LocalDateTime timestamp) {
        String sql = "INSERT INTO entries(body, timestamp) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, body);
            pstmt.setString(2, timestamp.toString());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error saving entry: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Loads all journal entries from the database into the entries list.
     */
    private void loadEntriesFromDatabase() {
        entries.clear();
        String sql = "SELECT id, body, timestamp FROM entries ORDER BY id ASC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String body = rs.getString("body");
                LocalDateTime timestamp = LocalDateTime.parse(rs.getString("timestamp"));
                entries.add(new Entry(id, body, timestamp));
            }
        } catch (SQLException e) {
            System.out.println("Error loading entries: " + e.getMessage());
        }
    }
}