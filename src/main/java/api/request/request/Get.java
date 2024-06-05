package api.request.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.concurrent.CompletableFuture;

import api.request.HttpRequester;
import api.request.error.ApiRequestException;

/**
 * The Get class provides functionality for executing GET requests.
 * 
 * @author William Beaudin
 */
public class Get extends HttpRequester {

    /**
     * Constructs a new instance of Get with the specified HttpRequester.
     * 
     * @param requester The HttpRequester instance.
     * @author William Beaudin
     */
    public Get(HttpRequester requester) {
        super(requester.getBaseUrl());
        this.objectMapper = requester.getObjectMapper();
        this.getHeaders().putAll(requester.getHeaders());
    }

    /**
     * Executes a GET request asynchronously with the specified endpoint.
     * 
     * @param endpoint The endpoint for the GET request.
     * @return A CompletableFuture containing the response from the GET request.
     * @author William Beaudin
     */
    public CompletableFuture<String> executeAsync(String endpoint) {
        return executeAsync(endpoint, "GET", null);
    }

    /**
     * Executes a GET request with the specified endpoint.
     * 
     * @param endpoint The endpoint for the GET request.
     * @return The response from the GET request.
     * @throws ApiRequestException If there is an error executing the request.
     * @author William Beaudin
     */
    public String execute(String endpoint) throws ApiRequestException {
        try {
            HttpURLConnection connection = createConnection(endpoint, "GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    return response.toString();
                }
            } else {
                throw new ApiRequestException("GET request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            throw new ApiRequestException("Error executing GET request", e);
        }
    }

    /**
     * Executes a GET request with the specified endpoint and deserializes the
     * response into the specified type.
     * 
     * @param <T>          The type to which the response should be deserialized.
     * @param endPoint     The endpoint for the GET request.
     * @param responseType The class type to which the response should be
     *                     deserialized.
     * @return The deserialized response object.
     * @throws ApiRequestException If there is an error executing the request or
     *                             deserializing the response.
     * @author William Beaudin
     */
    public <T> T executeAndDeserialize(String endPoint, Class<T> responseType) throws ApiRequestException {
        try {
            String jsonResponse = execute(endPoint);
            return objectMapper.readValue(jsonResponse, responseType);
        } catch (Exception e) {
            throw new ApiRequestException("Error executing GET request and deserializing response", e);
        }
    }
}