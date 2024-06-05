package qc.netconex.error;

/**
 * Exception class for errors during JSON parsing.
 */
public class JsonParsingException extends ApiRequestException {
    /**
     * Constructs a JsonParsingException with the specified detail message.
     * 
     * @param message The detail message (which is saved for later retrieval by the
     *                getMessage() method).
     */
    public JsonParsingException(String message) {
        super(message);
    }

    /**
     * Constructs a JsonParsingException with the specified detail message and
     * cause.
     * 
     * @param message The detail message (which is saved for later retrieval by the
     *                getMessage() method).
     * @param cause   The cause (which is saved for later retrieval by the
     *                getCause() method).
     */
    public JsonParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}