package com.proiectipdp.chess.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ChessServerClient {

    public record ServerMove(int fromRow, int fromCol, int toRow, int toCol) {}

    private final String httpBaseUrl;
    private final HttpClient httpClient;

    public ChessServerClient(String httpBaseUrl) {
        this.httpBaseUrl = httpBaseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public String joinGame(String playerId) throws Exception {
        String json =
                "{\n" +
                        "  \"id\": \"" + playerId + "\"\n" +
                        "}";

        return post("/game/join", json);
    }

    public String getGameByPlayer(String playerId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpBaseUrl + "/game/player/" + playerId))
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();

        return send(request);
    }

    public String sendMove(String gameId,
                           String playerId,
                           int fromRow,
                           int fromCol,
                           int toRow,
                           int toCol) throws Exception {

        String json =
                "{\n" +
                        "  \"playerId\": \"" + playerId + "\",\n" +
                        "  \"fromRow\": " + fromRow + ",\n" +
                        "  \"fromCol\": " + fromCol + ",\n" +
                        "  \"toRow\": " + toRow + ",\n" +
                        "  \"toCol\": " + toCol + "\n" +
                        "}";

        return post("/game/" + gameId + "/move", json);
    }

    public String getGameState(String gameId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpBaseUrl + "/game/" + gameId))
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();

        return send(request);
    }

    public String getGameHistory() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpBaseUrl + "/game/history"))
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();

        return send(request);
    }

    public String getSavedMoves(String gameId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpBaseUrl + "/game/" + gameId + "/saved-moves"))
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();

        return send(request);
    }

    public String endGame(String gameId, String result) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpBaseUrl + "/game/" + gameId + "/end?result=" + result))
                .POST(HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(5))
                .build();

        return send(request);
    }

    public String extractGameId(String response) {
        String prefix = "Game started! ID: ";

        if (response == null || !response.startsWith(prefix)) {
            return null;
        }

        return response.substring(prefix.length()).trim();
    }

    public String extractGameIdFromJson(String json) {
        return extractStringField(json, "gameId");
    }

    public String determinePlayerColor(String gameJson, String playerId) {
        if (gameJson == null || gameJson.equals("null")) {
            return "WHITE";
        }

        String player1Id = extractNestedPlayerId(gameJson, "player1");
        String player2Id = extractNestedPlayerId(gameJson, "player2");

        if (playerId.equals(player1Id)) {
            return "WHITE";
        }

        if (playerId.equals(player2Id)) {
            return "BLACK";
        }

        return "WHITE";
    }

    public List<ServerMove> extractMovesFromGameJson(String json) {
        List<ServerMove> moves = new ArrayList<>();

        if (json == null || json.equals("null")) {
            return moves;
        }

        int movesIndex = json.indexOf("\"moves\":[");

        if (movesIndex == -1) {
            return moves;
        }

        int arrayStart = json.indexOf("[", movesIndex);
        int arrayEnd = findMatchingBracket(json, arrayStart);

        if (arrayStart == -1 || arrayEnd == -1) {
            return moves;
        }

        String movesArray = json.substring(arrayStart + 1, arrayEnd);

        int index = 0;

        while (true) {
            int objectStart = movesArray.indexOf("{", index);

            if (objectStart == -1) {
                break;
            }

            int objectEnd = movesArray.indexOf("}", objectStart);

            if (objectEnd == -1) {
                break;
            }

            String moveObject = movesArray.substring(objectStart + 1, objectEnd);

            Integer fromRow = extractIntField(moveObject, "fromRow");
            Integer fromCol = extractIntField(moveObject, "fromCol");
            Integer toRow = extractIntField(moveObject, "toRow");
            Integer toCol = extractIntField(moveObject, "toCol");

            if (fromRow != null &&
                    fromCol != null &&
                    toRow != null &&
                    toCol != null) {

                moves.add(
                        new ServerMove(
                                fromRow,
                                fromCol,
                                toRow,
                                toCol
                        )
                );
            }

            index = objectEnd + 1;
        }

        return moves;
    }

    private int findMatchingBracket(String text, int openIndex) {
        if (openIndex == -1) {
            return -1;
        }

        int depth = 0;

        for (int i = openIndex; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (ch == '[') {
                depth++;
            } else if (ch == ']') {
                depth--;

                if (depth == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    private Integer extractIntField(String jsonObject, String fieldName) {
        String search = "\"" + fieldName + "\":";
        int start = jsonObject.indexOf(search);

        if (start == -1) {
            return null;
        }

        start += search.length();

        while (start < jsonObject.length() &&
                Character.isWhitespace(jsonObject.charAt(start))) {
            start++;
        }

        int end = start;

        while (end < jsonObject.length()) {
            char ch = jsonObject.charAt(end);

            if (!Character.isDigit(ch) && ch != '-') {
                break;
            }

            end++;
        }

        if (start == end) {
            return null;
        }

        return Integer.parseInt(jsonObject.substring(start, end));
    }

    private String extractNestedPlayerId(String json, String playerField) {
        String search = "\"" + playerField + "\":";
        int playerIndex = json.indexOf(search);

        if (playerIndex == -1) {
            return null;
        }

        int idIndex = json.indexOf("\"id\":\"", playerIndex);

        if (idIndex == -1) {
            return null;
        }

        idIndex += "\"id\":\"".length();

        int end = json.indexOf("\"", idIndex);

        if (end == -1) {
            return null;
        }

        return json.substring(idIndex, end);
    }

    private String extractStringField(String json, String fieldName) {
        if (json == null || json.equals("null")) {
            return null;
        }

        String search = "\"" + fieldName + "\":\"";
        int start = json.indexOf(search);

        if (start == -1) {
            return null;
        }

        start += search.length();

        int end = json.indexOf("\"", start);

        if (end == -1) {
            return null;
        }

        return json.substring(start, end);
    }

    private String post(String path, String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpBaseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .timeout(Duration.ofSeconds(5))
                .build();

        return send(request);
    }

    private String send(HttpRequest request) throws Exception {
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Server error: " + response.statusCode()
                    + "\n" + response.body());
        }

        return response.body();
    }
}
