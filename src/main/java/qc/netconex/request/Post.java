package qc.netconex.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import qc.netconex.NetConex;
import qc.netconex.error.ApiRequestException;

/**
 * The {@code Post} class provides functionality for executing POST requests.
 * 
 * <p>
 * This class extends {@code HttpRequester} and inherits its methods for making
 * HTTP requests.
 * 
 * @author William Beaudin
 */
public class Post extends NetConex {

    /**
     * Constructs a new instance of {@code Post} with the specified
     * {@code HttpRequester}.
     * 
     * @param requester The {@code HttpRequester} instance.
     */
    public Post(NetConex requester) {
        super(requester.getBaseUrl());
        this.objectMapper = super.getObjectMapper();
        this.getHeaders().putAll(requester.getHeaders());
    }

    /**
     * Executes a POST request with the specified endpoint and request body.
     * 
     * @param endpoint    The endpoint for the POST request.
     * @param requestBody The request body object.
     * @return The response from the POST request.
     * @throws ApiRequestException If there is an error executing the request.
     */
    @Override
    public String execute(String endpoint, Object requestBody) throws ApiRequestException {
        try {
            HttpURLConnection connection = createConnection(endpoint, "POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonInputString = objectMapper.writeValueAsString(requestBody);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
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
                throw new ApiRequestException("POST request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            throw new ApiRequestException("Error executing POST request", e);
        }
    }

    /**
     * Builds a request body from the given array and class type.
     * 
     * @param array     The array containing values for the request body fields.
     * @param classType The class type representing the request body.
     * @return A map representing the request body.
     * @throws Exception If there is an error building the request body.
     */
    public Map<String, Object> buildRequestBodyFromArray(String[] array, Class<?> classType) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();

        String[] fieldName = Arrays.stream(classType.getDeclaredFields())
                .map(Field::getName).toArray(String[]::new);

        for (int i = 0; i < array.length && i < fieldName.length; i++) {
            requestBody.put(fieldName[i], array[i]);
        }

        return requestBody;
    }
}
