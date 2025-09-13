import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single journal entry.
 * Each entry has a body text and timestamp, and generates a VBox
 * for display in the journal UI.
 *
 * Author: Sonya Pathania
 * Version: 2.0
 */
public class Entry {

    /**
     * The id number of the entry
     */
    private int id;

    /**
     * The text content of the entry
     */
    private String body;

    /**
     * The date and time of the entry
     */
    private LocalDateTime timestamp;

    /**
     * Formatter for displaying timestamps
     */
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");

    /**
     * Constructs a new Entry with the given body text and timestamp.
     *
     * @param id        the id of the entry
     * @param body      the text content of the entry
     * @param timestamp the date and time of the entry
     */
    public Entry(int id, String body, LocalDateTime timestamp) {
        this.id = id;
        this.body = body;
        this.timestamp = timestamp;
    }

    /**
     * Returns the body text of the entry.
     *
     * @return the entry body
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns the timestamp of the entry.
     *
     * @return the entry timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Generates a VBox containing the entry's timestamp and body,
     * styled for display in the journal UI.
     *
     * @return a VBox representing this entry
     */
    public VBox getEntry() {
        VBox box = new VBox(5);

        // Header shows timestamp in purple
        Label header = new Label(timestamp.format(DISPLAY_FORMATTER));
        header.setStyle("-fx-font-weight: bold; -fx-text-fill: #4b0082;");

        // Body content
        Label content = new Label(body);
        content.setWrapText(true);

        // Layout styling
        box.setStyle("-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1;" +
                "-fx-background-color: #f9f9f9; -fx-border-radius: 5; -fx-background-radius: 5;");

        box.getChildren().addAll(header, content);
        return box;
    }
}