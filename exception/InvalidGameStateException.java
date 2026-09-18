package exception;

/**
 * Custom UNCHECKED exception for programmer errors in game state. Indicates an
 * "impossible" state that should never occur in normal operation.
 *
 * This is an unchecked exception (extends RuntimeException) because it
 * represents a bug in the code rather than a recoverable error condition.
 * Examples include: - Attempting to spawn an object in an already occupied hole
 * - Accessing an invalid hole index - Game state inconsistencies
 */
public class InvalidGameStateException extends RuntimeException {

    /**
     * Constructs a new InvalidGameStateException with the specified message.
     *
     * @param message the detail message describing the invalid state
     */
    public InvalidGameStateException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidGameStateException with the specified message and
     * cause.
     *
     * @param message the detail message describing the invalid state
     * @param cause the underlying cause of this exception
     */
    public InvalidGameStateException(String message, Throwable cause) {
        super(message, cause);
    }
}