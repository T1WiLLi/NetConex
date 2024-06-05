package api.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import api.request.error.ApiRequestException;
import api.request.error.JsonFormattingException;
import api.request.error.JsonParsingException;
import api.request.request.Delete;
import api.request.request.Get;
import api.request.request.Methods;
import api.request.request.Post;
import api.request.request.Put;

/**
 * The HttpRequester class provides functionality to make HTTP requests.
 * It supports GET, POST, PUT, and DELETE methods.
 * 
 * @author William Beaudin
 */
public class HttpRequester implements Methods {
    private String baseUrl;
    private Map<String, String> headers = new HashMap<>();
    protected ObjectMapper objectMapper;
    private int timeout;

    /**
     * Constructs a new instance of HttpRequester with the specified base URL.
     * 
     * @param baseUrl The base URL for HTTP requests.
     * @author William Beaudin
     */
    public HttpRequester(String baseUrl) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.timeout = Integer.MAX_VALUE;
    }

    /**
     * Constructs a new instance of HttpRequester with the specified base URL.
     * 
     * @param baseUrl The base URL for HTTP requests.
     * @param timeout The specified time in millis before timeout for HTTP requests.
     * @author William Beaudin
     */
    public HttpRequester(String baseUrl, int timeout) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.timeout = timeout;
    }

    /**
     * Returns the base URL for HTTP requests.
     * 
     * @return The base URL.
     * @author William Beaudin
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns the headers set for the HTTP requester.
     * 
     * @return A map containing headers.
     * @author William Beaudin
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Returns the ObjectMapper used for JSON serialization/deserialization.
     * 
     * @return The ObjectMapper instance.
     * @author William Beaudin
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Sets a header with the specified key-value pair.
     * 
     * @param key   The header key.
     * @param value The header value.
     * @author William Beaudin
     */
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Sets the timeout duration for HTTP requests.
     * 
     * @param timeout The timeout duration in milliseconds.
     * @author William Beaudin
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Retrieves the timeout duration for HTTP requests.
     * 
     * @return The timeout duration in milliseconds.
     * @author William Beaudin
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Creates and configures a new HttpURLConnection for the specified endpoint and
     * HTTP method.
     * 
     * @param endpoint The endpoint relative to the base URL.
     * @param method   The HTTP method (e.g., GET, POST, PUT, DELETE).
     * @return HttpURLConnection configured for the specified endpoint and method.
     * @throws ApiRequestException If there is an error creating the connection.
     * @throws IOException
     * @throws URISyntaxException
     * @author William Beaudin
     */
    protected HttpURLConnection createConnection(String endpoint, String method)
            throws IOException, URISyntaxException {
        URI uri = new URI(baseUrl + endpoint);
        URL url = uri.toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        for (Entry<String, String> entry : this.headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        return connection;
    }

    /**
     * Executes an HTTP request asynchronously with the specified endpoint, method,
     * and request body.
     * 
     * @param endpoint    The endpoint for the HTTP request.
     * @param method      The HTTP method (e.g., GET, POST, PUT, DELETE).
     * @param requestBody The request body object (can be null for requests without
     *                    a body).
     * @return A CompletableFuture containing the response from the HTTP request.
     * @author William Beaudin
     */
    protected CompletableFuture<String> executeAsync(String endpoint, String method, Object requestBody) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = createConnection(endpoint, method);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                if (requestBody != null) {
                    String jsonInputString = objectMapper.writeValueAsString(requestBody);
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                }

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        return response.toString();
                    }
                } else {
                    throw new RuntimeException(method + " request failed with response code: " + responseCode);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error executing " + method + " request", e);
            }
        });
    }

    /**
     * Formats the given JSON response string for better readability.
     * 
     * @param jsonResponse The JSON response string to be formatted.
     * @return The formatted JSON string.
     * @throws JsonParsingException    If there is an error parsing the JSON.
     * @throws JsonFormattingException If there is an error formatting the JSON.
     * @author William Beaudin
     */
    @Override
    public String formatResponse(String jsonResponse) throws JsonParsingException, JsonFormattingException {
        try {
            Object jsonObject = objectMapper.readValue(jsonResponse, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (Exception e) {
            throw new JsonFormattingException("Error formatting JSON response", e);
        }
    }

    /**
     * Builds a request body from the given object.
     * 
     * @param object The object to be serialized into a request body.
     * @return A map representing the request body.
     * @throws ApiRequestException If there is an error building the request body.
     * @see Methods#buildRequestBodyFromObject(Object)
     * @author William Beaudin
     */
    @Override
    public Map<String, Object> buildRequestBodyFromObject(Object object) throws ApiRequestException {
        try {
            return objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new ApiRequestException("Error building request body from object", e);
        }
    }

    /**
     * Executes a GET request with the specified endpoint and object (deprecated).
     * This method is deprecated and will throw an UnsupportedOperationException if
     * called.
     * 
     * @param endpoint The endpoint for the GET request.
     * @param object   The object (deprecated) associated with the request.
     * @return The response from the GET request.
     * @throws UnsupportedOperationException If the method is called, as it is
     *                                       deprecated.
     * @throws ApiRequestException
     * @author William Beaudin
     */
    @Override
    @Deprecated
    public String execute(String endpoint, Object object) throws UnsupportedOperationException, ApiRequestException {
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    /**
     * Returns a new instance of the Get request handler.
     * 
     * @return A new instance of the Get class.
     * @author William Beaudin
     */
    public Get get() {
        return new Get(this);
    }

    /**
     * Returns a new instance of the Post request handler.
     * 
     * @return A new instance of the Post class.
     * @author William Beaudin
     */
    public Post post() {
        return new Post(this);
    }

    /**
     * Returns a new instance of the Put request handler.
     * 
     * @return A new instance of the Put class.
     * @author William Beaudin
     */
    public Put put() {
        return new Put(this);
    }

    /**
     * Returns a new instance of the Delete request handler.
     * 
     * @return A new instance of the Delete class.
     * @author William Beaudin
     */
    public Delete delete() {
        return new Delete(this);
    }
}
