package exception;

/**
 * Custom CHECKED exception for high score file operations. Wraps IOException
 * and ClassNotFoundException from serialization.
 *
 * This is a checked exception because file I/O errors are recoverable - the
 * application can continue with an empty high score list.
 */
public class HighScoreException extends Exception {

    /**
     * Constructs a new HighScoreException with the specified message.
     *
     * @param message the detail message
     */
    public HighScoreException(String message) {
        super(message);
    }

    /**
     * Constructs a new HighScoreException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the underlying cause (IOException or ClassNotFoundException)
     */
    public HighScoreException(String message, Throwable cause) {
        super(message, cause);
    }
}