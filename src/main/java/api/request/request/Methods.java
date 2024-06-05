package api.request.request;

import java.util.Map;

import api.request.error.ApiRequestException;

/**
 * The Methods interface defines common methods to be implemented by HTTP
 * request handlers (e.g., Get, Post, Put, Delete).
 * These methods provide functionality for formatting responses, executing
 * requests, and building request bodies.
 * 
 * @author William Beaudin
 */
public interface Methods {

    /**
     * Formats the given JSON response string for better readability.
     * 
     * @param jsonResponse The JSON response string to be formatted.
     * @return The formatted JSON string.
     * @throws ApiRequestException If there is an error parsing or formatting the
     *                             JSON.
     * @author William Beaudin
     */
    public String formatResponse(String jsonResponse) throws ApiRequestException;

    /**
     * Executes an HTTP request with the specified endpoint and request body object.
     * 
     * @param endpoint The endpoint for the HTTP request.
     * @param object   The request body object.
     * @return The response from the HTTP request.
     * @throws ApiRequestException If there is an error executing the request.
     * @author William Beaudin
     */
    public String execute(String endpoint, Object object) throws ApiRequestException;

    /**
     * Builds a request body from the given object.
     * 
     * @param object The object to be serialized into a request body.
     * @return A map representing the request body.
     * @throws ApiRequestException If there is an error building the request body.
     * @author William Beaudin
     */
    public Map<String, Object> buildRequestBodyFromObject(Object object) throws ApiRequestException;
}
