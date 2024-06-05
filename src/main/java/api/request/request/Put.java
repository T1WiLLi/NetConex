package api.request.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import api.request.HttpRequester;
import api.request.error.ApiRequestException;

/**
 * The Put class provides functionality for executing PUT requests.
 * 
 * @author William Beaudin
 */
public class Put extends HttpRequester {

    /**
     * Constructs a new instance of Put with the specified HttpRequester.
     * 
     * @param requester The HttpRequester instance.
     * @author William Beaudin
     */
    public Put(HttpRequester requester) {
        super(requester.getBaseUrl());
        this.objectMapper = requester.getObjectMapper();
        this.getHeaders().putAll(requester.getHeaders());
    }

    /**
     * Executes a PUT request with the specified endpoint and request body.
     * 
     * @param endpoint    The endpoint for the PUT request.
     * @param requestBody The request body object.
     * @return The response from the PUT request.
     * @throws ApiRequestException If there is an error executing the request.
     * @see Methods#execute(String, Object)
     * @author William Beaudin
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
