package qc.netconex.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.CompletableFuture;

import qc.netconex.HttpRequester;
import qc.netconex.error.ApiRequestException;

/**
 * The {@code Put} class provides functionality for executing PUT requests.
 * 
 * <p>
 * This class extends {@code HttpRequester} and inherits its methods for making
 * HTTP requests.
 * 
 * @author William Beaudin
 */
public class Put extends HttpRequester {

    /**
     * Constructs a new instance of {@code Put} with the specified
     * {@code HttpRequester}.
     * 
     * @param requester The {@code HttpRequester} instance.
     */
    public Put(HttpRequester requester) {
        super(requester.getBaseUrl());
        this.objectMapper = requester.getObjectMapper();
        this.getHeaders().putAll(requester.getHeaders());
    }

    /**
     * Executes a PUT request asynchronously with the specified endpoint and
     * request body.
     * 
     * @param endpoint    The endpoint for the PUT request.
     * @param requestBody The request body object.
     * @return A {@code CompletableFuture} containing the response from the PUT
     *         request.
     */
    public CompletableFuture<String> executeAsync(String endpoint, Object requestBody) {
        return super.executeAsync(endpoint, "PUT", requestBody);
    }

    /**
     * Executes a PUT request with the specified endpoint and request body.
     * 
     * @param endpoint    The endpoint for the PUT request.
     * @param requestBody The request body object.
     * @return The response from the PUT request.
     * @throws ApiRequestException If there is an error executing the request.
     */
    public String execute(String endpoint, Object requestBody) throws ApiRequestException {
        try {
            HttpURLConnection connection = createConnection(endpoint, "PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonInputString = objectMapper.writeValueAsString(requestBody);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

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
                connection.disconnect();
                throw new ApiRequestException("PUT request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            throw new ApiRequestException("Error executing PUT request", e);
        }
    }
}
