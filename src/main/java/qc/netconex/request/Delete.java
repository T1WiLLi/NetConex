package qc.netconex.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.concurrent.CompletableFuture;

import qc.netconex.HttpRequester;
import qc.netconex.error.ApiRequestException;

/**
 * The Delete class provides functionality for executing DELETE requests.
 * 
 * @author William Beaudin
 */
public class Delete extends HttpRequester {

    /**
     * Constructs a new instance of Delete with the specified HttpRequester.
     * 
     * @param requester The HttpRequester instance.
     * @author William Beaudin
     */
    public Delete(HttpRequester requester) {
        super(requester.getBaseUrl());
        this.objectMapper = requester.getObjectMapper();
        this.getHeaders().putAll(requester.getHeaders());
    }

    /**
     * Executes a DELETE request asynchronously with the specified endpoint.
     * 
     * @param endpoint The endpoint for the DELETE request.
     * @return A CompletableFuture containing the response from the DELETE request.
     * @author William Beaudin
     */
    public CompletableFuture<String> executeAsync(String endpoint) {
        return executeAsync(endpoint, "DELETE", null);
    }

    /**
     * Executes a DELETE request with the specified endpoint.
     * 
     * @param endpoint The endpoint for the DELETE request.
     * @return The response from the DELETE request.
     * @throws ApiRequestException If there is an error executing the request.
     * @author William Beaudin
     */
    public String execute(String endpoint) throws ApiRequestException {
        try {
            HttpURLConnection connection = createConnection(endpoint, "DELETE");
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
                throw new ApiRequestException("DELETE request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            throw new ApiRequestException("Error executing DELETE request", e);
        }
    }
}
