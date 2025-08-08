package server;
import com.google.gson.*;
import model.AuthData;
import model.UserData;
import model.GameData;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var request = new UserData(username, password, email);
        var json = makeRequest("/user", "POST", request, null);
        return new Gson().fromJson(json, AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        // email is ignored for login
        var request = new UserData(username, password, null);
        var json = makeRequest("/session", "POST", request, null);
        return new Gson().fromJson(json, AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("/session", "DELETE", null, authToken);
    }

    public List<GameData> listGames(String authToken) throws Exception {
        var json = makeRequest("/game", "GET", null, authToken);
        var obj = JsonParser.parseString(json).getAsJsonObject();
        var gamesArray = obj.getAsJsonArray("games");
        GameData[] games = new Gson().fromJson(gamesArray, GameData[].class);
        return Arrays.asList(games);
    }

    public int createGame(String gameName, String authToken) throws Exception {
        var request = Map.of("gameName", gameName);
        var json = makeRequest("/game", "POST", request, authToken);
        var obj = JsonParser.parseString(json).getAsJsonObject();
        return obj.get("gameID").getAsInt();
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws Exception {
        var request = Map.of("playerColor", playerColor, "gameID", gameID);
        makeRequest("/game", "PUT", request, authToken);
    }

    public void clear() throws Exception {
        makeRequest("/db", "DELETE", null, null);
    }

    // handling requests
    private String makeRequest(String path, String method, Object body, String authToken) throws Exception {
        URL url = new URL(serverUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        if (body != null) {
            try (var out = new OutputStreamWriter(connection.getOutputStream())) {
                new Gson().toJson(body, out);
                out.flush();
            }
        }

        int responseCode = connection.getResponseCode();
        InputStream responseStream = (responseCode >= 200 && responseCode < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();

        try (var in = new BufferedReader(new InputStreamReader(responseStream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            if (responseCode < 200 || responseCode >= 300) {
                String raw = response.toString().trim();
                try {
                    JsonObject errObj = JsonParser.parseString(raw).getAsJsonObject();
                    String message = errObj.has("message") ? errObj.get("message").getAsString() : "Unknown server error.";

                    while (message.startsWith("Error: ")) {
                        message = message.substring("Error: ".length()).trim();
                    }

                    throw new Exception("Error: " + message);
                } catch (Exception parseEx) {
                    if (raw.contains("\"message\"")) {
                        int start = raw.indexOf("\"message\"") + 9;
                        int quoteStart = raw.indexOf("\"", start);
                        int quoteEnd = raw.indexOf("\"", quoteStart + 1);
                        String message = raw.substring(quoteStart + 1, quoteEnd);

                        while (message.startsWith("Error: ")) {
                            message = message.substring("Error: ".length()).trim();
                        }

                        throw new Exception("Error: " + message);
                    }
                    throw new Exception("Error: " + raw);
                }
            }

            return response.toString();
        }
    }
}