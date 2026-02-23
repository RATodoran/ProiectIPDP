package com.proiectipdp.chess;

import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.GameStatus;
import com.proiectipdp.chess.core.rules.RulesEngine;
import com.proiectipdp.chess.ui.BoardView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        GameState state = new GameState();
        RulesEngine engine = new RulesEngine();

        BoardView boardView = new BoardView(state, engine);

        // UI (dreapta): turn + status + move list
        Label turnLabel = new Label();
        turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        turnLabel.setTextFill(Color.WHITE);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<String> movesList = new ListView<>();
        movesList.setPrefWidth(260);
        movesList.setPrefHeight(520);

        // Fundal închis + text deschis (pentru listă)
        movesList.setStyle(
                "-fx-background-color: #1E1E1E;" +
                        "-fx-control-inner-background: #1E1E1E;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 6;"
        );

        // fiecare rând: fundal închis, text deschis
        movesList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle(
                            "-fx-background-color: #1E1E1E;"
                    );
                } else {
                    setText(item);
                    setTextFill(Color.web("#EAEAEA"));
                    setStyle(
                            "-fx-background-color: #1E1E1E;" +
                                    "-fx-font-size: 14px;"
                    );
                }
            }
        });

        VBox rightPanel = new VBox(12, turnLabel, statusLabel, movesList);
        rightPanel.setPadding(new Insets(16));
        rightPanel.setStyle("-fx-background-color: #1E1E1E; -fx-background-radius: 12;");

        BorderPane root = new BorderPane();
        root.setCenter(boardView);
        root.setRight(rightPanel);
        root.setStyle("-fx-background-color: #121212;");

        // funcție de refresh UI text
        Runnable refresh = () -> {
            // turn
            turnLabel.setText(state.getTurn() == com.proiectipdp.chess.core.Color.WHITE ? "Turn: Alb" : "Turn: Negru");

            // status
            statusLabel.setText(statusText(state.getStatus()));
            statusLabel.setTextFill(statusColor(state.getStatus()));

            // moves: 2 mutări pe linie + index
            List<String> rows = formatMovesTwoPerLine(state.getMoveLog());
            movesList.setItems(FXCollections.observableArrayList(rows));

            if (!rows.isEmpty()) {
                movesList.scrollTo(rows.size() - 1);
            }
        };

        boardView.setOnStateChanged(refresh);
        refresh.run(); // inițial

        Scene scene = new Scene(root);
        stage.setTitle("Proiect Sah IPDP");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // Transformă [e4, e5, Cf3, Cc6] în ["1. e4 e5", "2. Cf3 Cc6"]
    private List<String> formatMovesTwoPerLine(List<String> moves) {
        List<String> rows = new ArrayList<>();

        int moveNumber = 1;
        for (int i = 0; i < moves.size(); i += 2) {
            String white = moves.get(i);
            String black = (i + 1 < moves.size()) ? moves.get(i + 1) : "";

            // aliniere simplă (opțional)
            // poți scoate padding-ul dacă nu-ți place
            String line = moveNumber + ". " + white + (black.isEmpty() ? "" : " " + black);

            rows.add(line);
            moveNumber++;
        }

        return rows;
    }

    private String statusText(GameStatus s) {
        return switch (s) {
            case CHECK -> "ȘAH";
            case CHECKMATE -> "ȘAH-MAT";
            case STALEMATE -> "PAT";
            default -> "";
        };
    }

    private Color statusColor(GameStatus s) {
        return switch (s) {
            case CHECK, CHECKMATE -> Color.web("#F2C14E"); // galben ca highlight-ul tău
            case STALEMATE -> Color.web("#BBBBBB");
            default -> Color.web("#1E1E1E");
        };
    }

    public static void main(String[] args) {
        launch();
    }
}