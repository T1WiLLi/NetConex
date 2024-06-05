package qc.netconex.error;

/**
 * Exception class for HTTP status code errors in API requests.
 */
public class HttpStatusCodeException extends ApiRequestException {
    private int statusCode;

    /**
     * Constructs a HttpStatusCodeException with the specified status code.
     * 
     * @param statusCode The HTTP status code.
     */
    public HttpStatusCodeException(int statusCode) {
        super("HTTP request failed with status code: " + statusCode);
        this.statusCode = statusCode;
    }

    /**
     * Constructs a HttpStatusCodeException with the specified status code and
     * message.
     * 
     * @param statusCode The HTTP status code.
     * @param message    The detail message (which is saved for later retrieval by
     *                   the getMessage() method).
     */
    public HttpStatusCodeException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Constructs a HttpStatusCodeException with the specified status code, message,
     * and cause.
     * 
     * @param statusCode The HTTP status code.
     * @param message    The detail message (which is saved for later retrieval by
     *                   the getMessage() method).
     * @param cause      The cause (which is saved for later retrieval by the
     *                   getCause() method).
     */
    public HttpStatusCodeException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Gets the HTTP status code associated with this exception.
     * 
     * @return The HTTP status code.
     */
    public int getStatusCode() {
        return statusCode;
    }
}