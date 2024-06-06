package qc.netconex.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.concurrent.CompletableFuture;

import qc.netconex.NetConex;
import qc.netconex.error.ApiRequestException;

/**
 * The {@code DELETE} class provides functionality for executing Delete
 * requests.
 * 
 * <p>
 * This class extends the functionality of the {@link NetConex} class,
 * allowing for the execution of Delete requests.
 * 
 * @author William Beaudin
 */
public class Delete extends NetConex {

    /**
     * Constructs a new instance of Delete with the specified HttpRequester.
     * 
     * @param requester The HttpRequester instance.
     */
    public Delete(NetConex requester) {
        super(requester.getBaseUrl());
        this.objectMapper = requester.getObjectMapper();
        this.getHeaders().putAll(requester.getHeaders());
    }

    /**
     * Executes a DELETE request with the specified endpoint.
     * 
     * @param endpoint The endpoint for the DELETE request.
     * @return The response from the DELETE request.
     * @throws ApiRequestException If there is an error executing the request.
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
