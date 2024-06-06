package qc.netconex;

import qc.netconex.error.ApiRequestException;
import qc.netconex.error.JsonFormattingException;
import qc.netconex.error.JsonParsingException;
import qc.netconex.request.Get;

public class Main {

    public static void main(String[] args) throws JsonParsingException, JsonFormattingException, ApiRequestException {
        NetConex netConex = new NetConex("https://dummyjson.com");

        Get getRequest = new Get(netConex);
        getRequest.executeAsync("/users", null)
                .thenApply(res -> {
                    try {
                        return getRequest.formatResponse(res);
                    } catch (JsonParsingException | JsonFormattingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenAccept(formatResponse -> {
                    if (formatResponse != null) {
                        System.out.println(formatResponse);
                    } else {
                        System.out.println("Formatting response failed.");
                    }
                }).join();
    }
}
