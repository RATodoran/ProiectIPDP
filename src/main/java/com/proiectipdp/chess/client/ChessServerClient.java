package com.proiectipdp.chess.client;

import java.net.URI;
import java.net.http.*;
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
        String json = """
                {
                  "id": "%s"
                }
                """.formatted(playerId);

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

        String json = """
                {
                  "playerId": "%s",
                  "fromRow": %d,
                  "fromCol": %d,
                  "toRow": %d,
                  "toCol": %d
                }
                """.formatted(playerId, fromRow, fromCol, toRow, toCol);

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
        if (json == null || json.equals("null")) {
            return null;
        }

        String search = "\"gameId\":\"";
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

    public String determinePlayerColor(String gameJson, String playerId) {
        if (gameJson == null || gameJson.equals("null")) {
            return "WHITE";
        }

        String player1Pattern = "\"player1\":{\"id\":\"" + playerId + "\"";
        String player2Pattern = "\"player2\":{\"id\":\"" + playerId + "\"";

        if (gameJson.contains(player1Pattern)) {
            return "WHITE";
        }

        if (gameJson.contains(player2Pattern)) {
            return "BLACK";
        }

        return "WHITE";
    }

    public List<ServerMove> extractMovesFromGameJson(String json) {
        List<ServerMove> moves = new ArrayList<>();

        if (json == null || json.equals("null")) {
            return moves;
        }

        int index = 0;

        while (true) {
            int fromRowIndex = json.indexOf("\"fromRow\":", index);

            if (fromRowIndex == -1) {
                break;
            }

            Integer fromRow = extractIntAfter(json, "\"fromRow\":", fromRowIndex);
            Integer fromCol = extractIntAfter(json, "\"fromCol\":", fromRowIndex);
            Integer toRow = extractIntAfter(json, "\"toRow\":", fromRowIndex);
            Integer toCol = extractIntAfter(json, "\"toCol\":", fromRowIndex);

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

            index = fromRowIndex + 1;
        }

        return moves;
    }

    private Integer extractIntAfter(String text, String field, int startFrom) {
        int start = text.indexOf(field, startFrom);

        if (start == -1) {
            return null;
        }

        start += field.length();

        while (start < text.length() && Character.isWhitespace(text.charAt(start))) {
            start++;
        }

        int end = start;

        while (end < text.length()) {
            char ch = text.charAt(end);

            if (!Character.isDigit(ch) && ch != '-') {
                break;
            }

            end++;
        }

        if (start == end) {
            return null;
        }

        return Integer.parseInt(text.substring(start, end));
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
