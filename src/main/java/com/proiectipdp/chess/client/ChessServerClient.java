package com.proiectipdp.chess.client;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class ChessServerClient {

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

    public String extractGameId(String response) {
        String prefix = "Game started! ID: ";

        if (response == null || !response.startsWith(prefix)) {
            return null;
        }

        return response.substring(prefix.length()).trim();
    }
}
