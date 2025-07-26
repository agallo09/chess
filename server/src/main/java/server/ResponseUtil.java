package server;

import com.google.gson.Gson;
import spark.Response;

import java.util.Map;

public class ResponseUtil {
    private static final Gson GSON = new Gson();

    public static String handleException(Response response, Exception e) {
        String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

        if (msg.contains("unauthorized")) {
            response.status(401);
        } else if (msg.contains("bad request")) {
            response.status(400);
        } else {
            response.status(500);
        }

        return GSON.toJson(Map.of("message", "Error: " + e.getMessage()));
    }
}