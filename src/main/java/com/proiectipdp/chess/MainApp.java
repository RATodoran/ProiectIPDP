package com.proiectipdp.chess;
import com.proiectipdp.chess.core.Move;
import com.proiectipdp.chess.core.Position;
import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.GameStatus;
import com.proiectipdp.chess.core.rules.RulesEngine;
import com.proiectipdp.chess.ui.BoardView;
import com.proiectipdp.chess.ui.BoardWithCoords;
import com.proiectipdp.chess.ui.MoveRow;
import com.proiectipdp.chess.core.Move;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {
    private Stage primaryStage;
    public void setScene(Scene scene) {
        primaryStage.setScene(scene);
    }
    @Override
    public void start(Stage stage) {

        this.primaryStage = stage;
        StartView startView = new StartView(this);

        stage.setTitle("Proiect Sah IPDP");
        stage.setScene(startView.getScene());
        stage.setResizable(false);
        stage.show();
    }

    public void showGame(int minutes, String color) {

        GameState state = new GameState();
        RulesEngine engine = new RulesEngine();

        // RANDOM FIRST
        if (color.equals("RANDOM")) {
            color = Math.random() < 0.5 ? "WHITE" : "BLACK";
        }

// mutare cu e2e4
        if (color.equals("BLACK")) {
            try {
                Move move = new Move(
                        new Position(6, 4), // e2
                        new Position(4, 4), // e4
                        null
                );

            } catch (Exception ex) {
                System.out.println("Eroare la mutarea default: " + ex.getMessage());
            }
        }

        BoardView boardView = new BoardView(state, engine);
        BoardWithCoords boardWithCoords = new BoardWithCoords(boardView);

        Label turnLabel = new Label();
        turnLabel.setStyle("-fx-font-size:18px;-fx-font-weight:bold;");
        turnLabel.setTextFill(Color.WHITE);

        int seconds = minutes * 60;

        IntegerProperty whiteTime = new SimpleIntegerProperty(seconds);
        IntegerProperty blackTime = new SimpleIntegerProperty(seconds);

        Label whiteClock = new Label("10:00");
        Label blackClock = new Label("10:00");
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size:20px;-fx-font-weight:bold;");

        whiteClock.setStyle("-fx-background-color:white;-fx-text-fill:black;-fx-font-size:22px;-fx-padding:10 20;-fx-background-radius:12;");
        blackClock.setStyle("-fx-background-color:black;-fx-text-fill:white;-fx-font-size:22px;-fx-padding:10 20;-fx-background-radius:12;");

        HBox clocks = new HBox(12, whiteClock, blackClock);
        clocks.setAlignment(Pos.CENTER);

        Timeline timer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    if (state.getTurn().name().equals("WHITE")) {
                        whiteTime.set(whiteTime.get() - 1);
                        whiteClock.setText(formatTime(whiteTime.get()));
                    } else {
                        blackTime.set(blackTime.get() - 1);
                        blackClock.setText(formatTime(blackTime.get()));
                    }
                })
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

        TableView<MoveRow> movesTable = new TableView<>();

        TableColumn<MoveRow,Integer> colNum = new TableColumn<>("#");
        colNum.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<MoveRow,String> colWhite = new TableColumn<>("Alb");
        colWhite.setCellValueFactory(new PropertyValueFactory<>("white"));

        TableColumn<MoveRow,String> colBlack = new TableColumn<>("Negru");
        colBlack.setCellValueFactory(new PropertyValueFactory<>("black"));

        movesTable.getColumns().addAll(colNum, colWhite, colBlack);
        movesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        movesTable.setPrefHeight(500);

        Button drawBtn = new Button("Remiză");
        Button resignBtn = new Button("Cedează");
        Button flagBtn = new Button("Flag");
        Button flipBtn = new Button("Flip");

        Button startBtn = new Button("⏮");
        Button prevBtn = new Button("◀");
        Button nextBtn = new Button("▶");
        Button endBtn = new Button("⏭");

        CheckBox highlightMoves = new CheckBox("Highlight moves");
        CheckBox allowIllegal = new CheckBox("Allow illegal moves");

        highlightMoves.setTextFill(Color.WHITE);
        allowIllegal.setTextFill(Color.WHITE);

        highlightMoves.setOnAction(e ->
                boardView.setShowMoves(highlightMoves.isSelected())
        );

        allowIllegal.setOnAction(e ->
                boardView.setAllowIllegal(allowIllegal.isSelected())
        );

        flagBtn.setOnAction(e -> {
            if (boardView.hasIllegalMove()) {
                new Alert(Alert.AlertType.INFORMATION, "Ai câștigat! Mutare ilegală").showAndWait();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Nu există mutare ilegală").showAndWait();
            }
        });

        flipBtn.setOnAction(e -> {
            boardView.flipBoard();
            boardWithCoords.refreshCoords();
        });

        HBox controls = new HBox(8, drawBtn, resignBtn, flagBtn, flipBtn);
        controls.setAlignment(Pos.CENTER);

        HBox toggles = new HBox(10, highlightMoves, allowIllegal);
        toggles.setAlignment(Pos.CENTER);

        HBox navigation = new HBox(8, startBtn, prevBtn, nextBtn, endBtn);
        navigation.setAlignment(Pos.CENTER);

        VBox rightPanel = new VBox(
                12,
                clocks,
                controls,
                toggles,
                turnLabel,
                statusLabel,
                movesTable,
                navigation
        );
        if (color.equals("RANDOM")) {
            color = Math.random() < 0.5 ? "WHITE" : "BLACK";
        }
        rightPanel.setPadding(new Insets(16));
        rightPanel.setStyle("-fx-background-color:#1E1E1E;");

        BorderPane root = new BorderPane();
        root.setCenter(boardWithCoords);
        root.setRight(rightPanel);

        Runnable refresh = () -> {
            turnLabel.setText(
                    state.getTurn().name().equals("WHITE")
                            ? "Turn: Alb"
                            : "Turn: Negru"
            );

            statusLabel.setText(statusText(state.getStatus()));
            statusLabel.setTextFill(statusColor(state.getStatus()));

            List<String> moves = state.getMoveLog();
            List<MoveRow> rows = new ArrayList<>();

            int number = 1;

            for (int i = 0; i < moves.size(); i += 2) {
                String white = moves.get(i);
                String black = (i + 1 < moves.size()) ? moves.get(i + 1) : "";
                rows.add(new MoveRow(number, white, black));
                number++;
            }

            movesTable.setItems(FXCollections.observableArrayList(rows));

            startBtn.setDisable(!state.getHistory().canPrev());
            prevBtn.setDisable(!state.getHistory().canPrev());
            nextBtn.setDisable(!state.getHistory().canNext());
            endBtn.setDisable(!state.getHistory().canNext());
        };

        boardView.setOnStateChanged(refresh);

        startBtn.setOnAction(e -> {
            while (state.getHistory().canPrev()) {
                state.loadSnapshot(state.getHistory().prev());
            }
            boardView.deselectForExternal();
            boardView.forceRender();
            refresh.run();
        });

        prevBtn.setOnAction(e -> {
            var snap = state.getHistory().prev();
            if (snap != null) {
                state.loadSnapshot(snap);
                boardView.deselectForExternal();
                boardView.forceRender();
                refresh.run();
            }
        });

        nextBtn.setOnAction(e -> {
            var snap = state.getHistory().next();
            if (snap != null) {
                state.loadSnapshot(snap);
                boardView.deselectForExternal();
                boardView.forceRender();
                refresh.run();
            }
        });

        endBtn.setOnAction(e -> {
            while (state.getHistory().canNext()) {
                state.loadSnapshot(state.getHistory().next());
            }
            boardView.deselectForExternal();
            boardView.forceRender();
            refresh.run();
        });

        refresh.run();

        Scene scene = new Scene(root, 1100, 750);

        primaryStage.setScene(scene);
    }


/*
    @Override

    public void start(Stage stage) {

        GameState state = new GameState();
        RulesEngine engine = new RulesEngine();

        BoardView boardView = new BoardView(state, engine);
        BoardWithCoords boardWithCoords = new BoardWithCoords(boardView);

        Label turnLabel = new Label();
        turnLabel.setStyle("-fx-font-size:18px;-fx-font-weight:bold;");
        turnLabel.setTextFill(Color.WHITE);

        IntegerProperty whiteTime = new SimpleIntegerProperty(600);
        IntegerProperty blackTime = new SimpleIntegerProperty(600);

        Label whiteClock = new Label("10:00");
        Label blackClock = new Label("10:00");
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size:20px;-fx-font-weight:bold;");

        whiteClock.setStyle("-fx-background-color:white;-fx-text-fill:black;-fx-font-size:22px;-fx-padding:10 20;-fx-background-radius:12;");
        blackClock.setStyle("-fx-background-color:black;-fx-text-fill:white;-fx-font-size:22px;-fx-padding:10 20;-fx-background-radius:12;");

        HBox clocks = new HBox(12, whiteClock, blackClock);
        clocks.setAlignment(Pos.CENTER);

        Timeline timer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    if (state.getTurn().name().equals("WHITE")) {
                        whiteTime.set(whiteTime.get() - 1);
                        whiteClock.setText(formatTime(whiteTime.get()));
                    } else {
                        blackTime.set(blackTime.get() - 1);
                        blackClock.setText(formatTime(blackTime.get()));
                    }
                })
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

        // ===== TABLE =====
        TableView<MoveRow> movesTable = new TableView<>();

        TableColumn<MoveRow,Integer> colNum = new TableColumn<>("#");
        colNum.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<MoveRow,String> colWhite = new TableColumn<>("Alb");
        colWhite.setCellValueFactory(new PropertyValueFactory<>("white"));

        TableColumn<MoveRow,String> colBlack = new TableColumn<>("Negru");
        colBlack.setCellValueFactory(new PropertyValueFactory<>("black"));

        movesTable.getColumns().addAll(colNum, colWhite, colBlack);
        movesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        movesTable.setPrefHeight(500);

        // ===== BUTTONS =====
        Button drawBtn = new Button("Remiză");
        Button resignBtn = new Button("Cedează");
        Button flagBtn = new Button("Flag");
        Button flipBtn = new Button("Flip");

        Button startBtn = new Button("⏮");
        Button prevBtn = new Button("◀");
        Button nextBtn = new Button("▶");
        Button endBtn = new Button("⏭");

        // ===== CHECKBOX =====
        CheckBox highlightMoves = new CheckBox("Highlight moves");
        CheckBox allowIllegal = new CheckBox("Allow illegal moves");

        highlightMoves.setTextFill(Color.WHITE);
        allowIllegal.setTextFill(Color.WHITE);

        highlightMoves.setOnAction(e ->
                boardView.setShowMoves(highlightMoves.isSelected())
        );

        allowIllegal.setOnAction(e ->
                boardView.setAllowIllegal(allowIllegal.isSelected())
        );

        // ===== BUTTON ACTIONS =====
        flagBtn.setOnAction(e -> {
            if (boardView.hasIllegalMove()) {
                new Alert(Alert.AlertType.INFORMATION, "Ai câștigat! Mutare ilegală").showAndWait();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Nu există mutare ilegală").showAndWait();
            }
        });

        flipBtn.setOnAction(e -> {
            boardView.flipBoard();
            boardWithCoords.refreshCoords();
        });

        // ===== LAYOUT =====
        HBox controls = new HBox(8, drawBtn, resignBtn, flagBtn, flipBtn);
        controls.setAlignment(Pos.CENTER);

        HBox toggles = new HBox(10, highlightMoves, allowIllegal);
        toggles.setAlignment(Pos.CENTER);

        HBox navigation = new HBox(8, startBtn, prevBtn, nextBtn, endBtn);
        navigation.setAlignment(Pos.CENTER);

        VBox rightPanel = new VBox(
                12,
                clocks,
                controls,
                toggles,
                turnLabel,
                statusLabel,
                movesTable,
                navigation
        );

        rightPanel.setPadding(new Insets(16));
        rightPanel.setStyle("-fx-background-color:#1E1E1E;");

        BorderPane root = new BorderPane();
        root.setCenter(boardWithCoords);
        root.setRight(rightPanel);

        // ===== REFRESH =====
        Runnable refresh = () -> {

            turnLabel.setText(
                    state.getTurn().name().equals("WHITE")
                            ? "Turn: Alb"
                            : "Turn: Negru"
            );

            statusLabel.setText(statusText(state.getStatus()));
            statusLabel.setTextFill(statusColor(state.getStatus()));

            List<String> moves = state.getMoveLog();
            List<MoveRow> rows = new ArrayList<>();

            int number = 1;

            for (int i = 0; i < moves.size(); i += 2) {
                String white = moves.get(i);
                String black = (i + 1 < moves.size()) ? moves.get(i + 1) : "";
                rows.add(new MoveRow(number, white, black));
                number++;
            }

            movesTable.setItems(FXCollections.observableArrayList(rows));

            startBtn.setDisable(!state.getHistory().canPrev());
            prevBtn.setDisable(!state.getHistory().canPrev());

            nextBtn.setDisable(!state.getHistory().canNext());
            endBtn.setDisable(!state.getHistory().canNext());
        };

        boardView.setOnStateChanged(refresh);

        // ===== NAVIGATION BUTTONS =====
        startBtn.setOnAction(e -> {
            while (state.getHistory().canPrev()) {
                state.loadSnapshot(state.getHistory().prev());
            }
            boardView.deselectForExternal();
            boardView.forceRender();
            refresh.run();
        });

        prevBtn.setOnAction(e -> {
            var snap = state.getHistory().prev();
            if (snap != null) {
                state.loadSnapshot(snap);
                boardView.deselectForExternal();
                boardView.forceRender();
                refresh.run();
            }
        });

        nextBtn.setOnAction(e -> {
            var snap = state.getHistory().next();
            if (snap != null) {
                state.loadSnapshot(snap);
                boardView.deselectForExternal();
                boardView.forceRender();
                refresh.run();
            }
        });

        endBtn.setOnAction(e -> {
            while (state.getHistory().canNext()) {
                state.loadSnapshot(state.getHistory().next());
            }
            boardView.deselectForExternal();
            boardView.forceRender();
            refresh.run();
        });

        refresh.run();

        Scene scene = new Scene(root, 1100, 750);

        stage.setTitle("Proiect Sah IPDP");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
*/
    private String statusText(GameStatus s){
        return switch(s){
            case CHECK -> "ȘAH";
            case CHECKMATE -> "ȘAH-MAT";
            case STALEMATE -> "PAT";
            default -> "";
        };
    }

    private Color statusColor(GameStatus s){
        return switch(s){
            case CHECK, CHECKMATE -> Color.web("#F2C14E");
            case STALEMATE -> Color.web("#BBBBBB");
            default -> Color.web("#1E1E1E");
        };
    }

    private String formatTime(int seconds){
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    public static void main(String[] args){
        launch();
    }
}