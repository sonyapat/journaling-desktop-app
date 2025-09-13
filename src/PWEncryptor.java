import org.mindrot.jbcrypt.BCrypt;

/**
 * This class contains password hashing functionality for user authentication.
 *
 * Author: Sonya Pathania
 * Version: 2.0
 */
public class PWEncryptor {

    /**
     * Constructor to create an Encryptor Object.
     */
    public PWEncryptor() {
    }

    /**
     * Method that encodes a password string using bcrypt.
     *
     * @param input The string (password) to encode.
     * @param parameter Ignored (kept for compatibility).
     * @return Returns the bcrypt hash of the password.
     */
    public String encode(String input, int parameter) {
        return hashPassword(input);
    }

    /**
     * Hashes a password using bcrypt.
     *
     * @param password The password to hash.
     * @return The hashed password.
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifies a password against a stored bcrypt hash.
     *
     * @param password The password entered by the user.
     * @param hashed The stored bcrypt hash.
     * @return True if the password matches the hash; false otherwise.
     */
    public boolean checkPassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }
}