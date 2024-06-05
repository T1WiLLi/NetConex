package qc.netconex.error;

/**
 * Exception class for errors during JSON formatting.
 */
public class JsonFormattingException extends ApiRequestException {
    /**
     * Constructs a JsonFormattingException with the specified detail message.
     * 
     * @param message The detail message (which is saved for later retrieval by the
     *                getMessage() method).
     */
    public JsonFormattingException(String message) {
        super(message);
    }

    /**
     * Constructs a JsonFormattingException with the specified detail message and
     * cause.
     * 
     * @param message The detail message (which is saved for later retrieval by the
     *                getMessage() method).
     * @param cause   The cause (which is saved for later retrieval by the
     *                getCause() method).
     */
    public JsonFormattingException(String message, Throwable cause) {
        super(message, cause);
    }
}