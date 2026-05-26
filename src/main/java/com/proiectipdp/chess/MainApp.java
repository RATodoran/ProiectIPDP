package com.proiectipdp.chess;

import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.GameStatus;
import com.proiectipdp.chess.core.rules.RulesEngine;
import com.proiectipdp.chess.ui.BoardView;
import com.proiectipdp.chess.ui.BoardWithCoords;
import com.proiectipdp.chess.ui.MoveRow;
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
import javafx.scene.effect.DropShadow;
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

    public void showGame(int minutes, String color, String gameId, String playerId, boolean allowIllegalMoves) {

        GameState state = new GameState();
        RulesEngine engine = new RulesEngine();

        if (color.equals("RANDOM")) {
            color = Math.random() < 0.5 ? "WHITE" : "BLACK";
        }
        final String playerColor = color;
        final int[] syncedMoveCount = {0};

        BoardView boardView = new BoardView(state, engine);
        boardView.setAllowIllegal(allowIllegalMoves);

        boardView.setOnMoveMade(moveInfo -> {

            // mutarea a fost deja aplicată local, deci o considerăm sincronizată local
            // ca polling-ul să nu încerce să o aplice încă o dată
            syncedMoveCount[0]++;

            new Thread(() -> {

                try {

                    com.proiectipdp.chess.client.ChessServerClient serverClient =
                            new com.proiectipdp.chess.client.ChessServerClient(
                                    "http://localhost:8081"
                            );

                    String response = serverClient.sendMove(
                            gameId,
                            playerId,
                            moveInfo.fromRow(),
                            moveInfo.fromCol(),
                            moveInfo.toRow(),
                            moveInfo.toCol()
                    );

                    System.out.println("Mutare trimisă către server: " + response);

                    if (!"Move added".equals(response)) {
                        System.out.println("Serverul nu a acceptat mutarea: " + response);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }).start();
        });
        if (color.equals("BLACK")) {
            boardView.flipBoard();
        }

        BoardWithCoords boardWithCoords = new BoardWithCoords(boardView);

        final Runnable[] refreshRef = new Runnable[1];

        Label gameTitle = new Label("♛ Chess Match");
        gameTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label gameSubtitle = new Label(color.equals("WHITE") ? "Joci cu piesele albe" : "Joci cu piesele negre");
        gameSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #bfc7d0;");

        VBox titleBox = new VBox(3, gameTitle, gameSubtitle);
        titleBox.setAlignment(Pos.CENTER);

        Label turnLabel = new Label();
        turnLabel.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        statusLabel.setMinHeight(26);
        statusLabel.setAlignment(Pos.CENTER);

        int seconds = minutes * 60;

        IntegerProperty whiteTime = new SimpleIntegerProperty(seconds);
        IntegerProperty blackTime = new SimpleIntegerProperty(seconds);

        Label whiteClock = new Label(formatTime(seconds));
        Label blackClock = new Label(formatTime(seconds));

        styleWhiteClock(whiteClock);
        styleBlackClock(blackClock);

        VBox whiteClockBox = clockCard("Alb", whiteClock);
        VBox blackClockBox = clockCard("Negru", blackClock);

        HBox clocks = new HBox(14, whiteClockBox, blackClockBox);
        clocks.setAlignment(Pos.CENTER);

        final boolean[] gameOverByTime = {false};
        final Timeline[] timerRef = new Timeline[1];

        timerRef[0] = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    if (gameOverByTime[0]) return;

                    if (state.getTurn().name().equals("WHITE")) {
                        whiteTime.set(whiteTime.get() - 1);

                        if (whiteTime.get() <= 0) {
                            whiteTime.set(0);
                            whiteClock.setText(formatTime(whiteTime.get()));
                            gameOverByTime[0] = true;
                            timerRef[0].stop();

                            boardView.setDisable(true);
                            boardView.setMouseTransparent(true);

                            statusLabel.setText("Negru a câștigat la timp!");
                            statusLabel.setTextFill(Color.web("#F2C14E"));

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Final de joc");
                            alert.setHeaderText("Timp expirat");
                            alert.setContentText("Negru a câștigat la timp!");
                            alert.showAndWait();
                            return;
                        }

                        whiteClock.setText(formatTime(whiteTime.get()));
                    } else {
                        blackTime.set(blackTime.get() - 1);

                        if (blackTime.get() <= 0) {
                            blackTime.set(0);
                            blackClock.setText(formatTime(blackTime.get()));
                            gameOverByTime[0] = true;
                            timerRef[0].stop();

                            boardView.setDisable(true);
                            boardView.setMouseTransparent(true);

                            statusLabel.setText("Alb a câștigat la timp!");
                            statusLabel.setTextFill(Color.web("#F2C14E"));

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Final de joc");
                            alert.setHeaderText("Timp expirat");
                            alert.setContentText("Alb a câștigat la timp!");
                            alert.showAndWait();
                            return;
                        }

                        blackClock.setText(formatTime(blackTime.get()));
                    }
                })
        );

        timerRef[0].setCycleCount(Timeline.INDEFINITE);
        timerRef[0].play();

        TableView<MoveRow> movesTable = new TableView<>();

        TableColumn<MoveRow, Integer> colNum = new TableColumn<>("#");
        colNum.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<MoveRow, String> colWhite = new TableColumn<>("Alb");
        colWhite.setCellValueFactory(new PropertyValueFactory<>("white"));

        TableColumn<MoveRow, String> colBlack = new TableColumn<>("Negru");
        colBlack.setCellValueFactory(new PropertyValueFactory<>("black"));

        colWhite.setCellFactory(column -> {
            TableCell<MoveRow, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && cell.getItem() != null && !cell.getItem().isBlank()) {
                    MoveRow row = cell.getTableView().getItems().get(cell.getIndex());
                    jumpToMove(state, boardView, row.getNumber() * 2 - 1);

                    if (refreshRef[0] != null) {
                        refreshRef[0].run();
                    }
                }
            });

            return cell;
        });

        colBlack.setCellFactory(column -> {
            TableCell<MoveRow, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && cell.getItem() != null && !cell.getItem().isBlank()) {
                    MoveRow row = cell.getTableView().getItems().get(cell.getIndex());
                    jumpToMove(state, boardView, row.getNumber() * 2);

                    if (refreshRef[0] != null) {
                        refreshRef[0].run();
                    }
                }
            });

            return cell;
        });

        movesTable.getColumns().addAll(colNum, colWhite, colBlack);
        movesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        movesTable.setPrefHeight(390);
        movesTable.setPlaceholder(new Label("Mutările vor apărea aici"));
        movesTable.setStyle(
                "-fx-background-color: rgba(255,255,255,0.06);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(244,210,122,0.25);" +
                        "-fx-border-radius: 16;" +
                        "-fx-control-inner-background: #171b22;" +
                        "-fx-table-cell-border-color: rgba(255,255,255,0.08);" +
                        "-fx-selection-bar: #c9963e;" +
                        "-fx-selection-bar-non-focused: #8e5c1d;"
        );

        Button drawBtn = new Button("Remiză");
        drawBtn.setOnAction(e -> {

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

            confirm.setTitle("Confirmare remiză");
            confirm.setHeaderText("Sigur dorești remiză?");
            confirm.setContentText("Partida se va termina și ceasul se va opri.");

            ButtonType yes = new ButtonType("Da");
            ButtonType no = new ButtonType("Nu");

            confirm.getButtonTypes().setAll(yes, no);

            confirm.showAndWait().ifPresent(response -> {

                if (response == yes) {

                    finishLocalGame(
                            boardView,
                            timerRef,
                            gameOverByTime,
                            statusLabel,
                            "Remiză!",
                            "#BBBBBB"
                    );

                    new Thread(() -> {
                        try {
                            com.proiectipdp.chess.client.ChessServerClient serverClient =
                                    new com.proiectipdp.chess.client.ChessServerClient("http://localhost:8081");

                            String serverResponse = serverClient.endGame(gameId, "DRAW");
                            System.out.println("Răspuns remiză: " + serverResponse);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }).start();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Final de joc");
                    alert.setHeaderText(null);
                    alert.setContentText("Partida s-a terminat remiză.");

                    alert.showAndWait();
                }
            });
        });

        Button resignBtn = new Button("Cedează");
        resignBtn.setOnAction(e -> {

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

            confirm.setTitle("Confirmare");
            confirm.setHeaderText("Sigur dorești să cedezi?");
            confirm.setContentText("Partida se va termina.");

            ButtonType yes = new ButtonType("Da");
            ButtonType no = new ButtonType("Nu");

            confirm.getButtonTypes().setAll(yes, no);

            confirm.showAndWait().ifPresent(response -> {

                if (response == yes) {

                    String winner;
                    String resultCode;

                    if (playerColor.equals("WHITE")) {
                        winner = "Negru";
                        resultCode = "BLACK_WIN";
                    } else {
                        winner = "Alb";
                        resultCode = "WHITE_WIN";
                    }

                    finishLocalGame(
                            boardView,
                            timerRef,
                            gameOverByTime,
                            statusLabel,
                            winner + " câștigă prin cedare!",
                            "#F2C14E"
                    );

                    new Thread(() -> {
                        try {
                            com.proiectipdp.chess.client.ChessServerClient serverClient =
                                    new com.proiectipdp.chess.client.ChessServerClient("http://localhost:8081");

                            String serverResponse = serverClient.endGame(gameId, resultCode);
                            System.out.println("Răspuns cedare: " + serverResponse);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }).start();

                    Alert result = new Alert(Alert.AlertType.INFORMATION);

                    result.setTitle("Final de joc");
                    result.setHeaderText(null);
                    result.setContentText(winner + " a câștigat!");

                    result.showAndWait();
                }
            });
        });

        Button flagBtn = new Button("Flag");
        Button flipBtn = new Button("Flip");

        styleGoldButton(drawBtn);
        styleDarkButton(resignBtn);
        styleDarkButton(flagBtn);
        styleDarkButton(flipBtn);


        Button startBtn = new Button("⏮");
        Button prevBtn = new Button("◀");
        Button nextBtn = new Button("▶");
        Button endBtn = new Button("⏭");

        styleNavButton(startBtn);
        styleNavButton(prevBtn);
        styleNavButton(nextBtn);
        styleNavButton(endBtn);

        CheckBox highlightMoves = new CheckBox("Highlight moves");
        styleCheckBox(highlightMoves);
        highlightMoves.setOnAction(e -> boardView.setShowMoves(highlightMoves.isSelected()));
        flagBtn.setOnAction(e -> {
            if (boardView.hasIllegalMove()) {
                String illegalColor = boardView.getIllegalMoveColor();
                String winner;

                if ("WHITE".equals(illegalColor)) {
                    winner = "Negru";
                } else {
                    winner = "Alb";
                }

                new Alert(Alert.AlertType.INFORMATION,
                        winner + " a câștigat! A fost semnalată o mutare ilegală.")
                        .showAndWait();

                boardView.setDisable(true);
                boardView.setMouseTransparent(true);
            } else {
                new Alert(Alert.AlertType.INFORMATION,
                        "Nu există mutare ilegală de semnalat.")
                        .showAndWait();
            }
        });

        flipBtn.setOnAction(e -> {
            boardView.flipBoard();
            boardWithCoords.refreshCoords();
        });

        HBox controls = new HBox(10, drawBtn, resignBtn, flagBtn, flipBtn);
        controls.setAlignment(Pos.CENTER);

        HBox toggles = new HBox(14, highlightMoves);
        toggles.setAlignment(Pos.CENTER);

        HBox navigation = new HBox(8, startBtn, prevBtn, nextBtn, endBtn);
        navigation.setAlignment(Pos.CENTER);

        VBox stateBox = new VBox(5, turnLabel, statusLabel);
        stateBox.setAlignment(Pos.CENTER);
        stateBox.setPadding(new Insets(10));
        stateBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.055);" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: rgba(255,255,255,0.08);" +
                        "-fx-border-radius: 18;"
        );

        Label historyTitle = new Label("Istoric mutări");
        historyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f5d58b;");

        VBox historyBox = new VBox(8, historyTitle, movesTable, navigation);
        historyBox.setAlignment(Pos.CENTER);

        VBox rightPanel = new VBox(
                16,
                titleBox,
                clocks,
                controls,
                toggles,
                stateBox,
                historyBox
        );
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setPadding(new Insets(24));
        rightPanel.setPrefWidth(350);
        rightPanel.setStyle(
                "-fx-background-color: rgba(18, 22, 27, 0.95);" +
                        "-fx-background-radius: 28;" +
                        "-fx-border-color: rgba(218, 165, 75, 0.38);" +
                        "-fx-border-radius: 28;" +
                        "-fx-border-width: 1.2;"
        );
        rightPanel.setEffect(softShadow());

        StackPane boardCard = new StackPane(boardWithCoords);
        boardCard.setAlignment(Pos.CENTER);
        boardCard.setPadding(new Insets(18));
        boardCard.setStyle(
                "-fx-background-color: rgba(18, 22, 27, 0.88);" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-color: rgba(244, 210, 122, 0.30);" +
                        "-fx-border-radius: 30;" +
                        "-fx-border-width: 1.2;"
        );
        boardCard.setEffect(softShadow());

        Label bgPiecesTop = new Label("♔     ♕     ♖     ♘     ♗     ♙");
        bgPiecesTop.setTextFill(Color.rgb(255, 255, 255, 0.055));
        bgPiecesTop.setStyle("-fx-font-size: 54px;");

        Label bgPiecesBottom = new Label("♟     ♝     ♞     ♜     ♛     ♚");
        bgPiecesBottom.setTextFill(Color.rgb(218, 165, 75, 0.08));
        bgPiecesBottom.setStyle("-fx-font-size: 52px;");

        BorderPane content = new BorderPane();
        content.setPadding(new Insets(24));
        content.setCenter(boardCard);
        content.setRight(rightPanel);
        BorderPane.setMargin(boardCard, new Insets(0, 24, 0, 0));

        VBox centerWithDecor = new VBox(8, bgPiecesTop, content, bgPiecesBottom);
        centerWithDecor.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(centerWithDecor);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0d1117, #161b22, #252018);");

        Runnable refresh = () -> {
            turnLabel.setText(
                    state.getTurn().name().equals("WHITE")
                            ? "Turn: Alb"
                            : "Turn: Negru"
            );

            if (!gameOverByTime[0]) {
                statusLabel.setText(statusText(state.getStatus()));
                statusLabel.setTextFill(statusColor(state.getStatus()));

                if (state.getStatus() == GameStatus.CHECKMATE ||
                        state.getStatus() == GameStatus.STALEMATE) {

                    gameOverByTime[0] = true;

                    if (timerRef[0] != null) {
                        timerRef[0].stop();
                    }

                    boardView.setDisable(true);
                    boardView.setMouseTransparent(true);
                }
            }

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

            updateBoardInputState(
                    boardView,
                    state,
                    playerColor,
                    gameOverByTime[0]
            );
        };

        refreshRef[0] = refresh;

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

        startRemoteMovePolling(
                gameId,
                boardView,
                syncedMoveCount,
                refreshRef,
                gameOverByTime,
                timerRef,
                statusLabel
        );

        Scene scene = new Scene(root, 1200, 820);
        primaryStage.setScene(scene);
    }


    private void finishLocalGame(BoardView boardView,
                                 Timeline[] timerRef,
                                 boolean[] gameOverByTime,
                                 Label statusLabel,
                                 String message,
                                 String colorHex) {

        gameOverByTime[0] = true;

        if (timerRef[0] != null) {
            timerRef[0].stop();
        }

        boardView.setDisable(true);
        boardView.setMouseTransparent(true);

        statusLabel.setText(message);
        statusLabel.setTextFill(Color.web(colorHex));
    }

    private void startRemoteMovePolling(String gameId,
                                        BoardView boardView,
                                        int[] syncedMoveCount,
                                        Runnable[] refreshRef,
                                        boolean[] gameOverByTime,
                                        Timeline[] timerRef,
                                        Label statusLabel) {

        Thread pollingThread = new Thread(() -> {

            com.proiectipdp.chess.client.ChessServerClient serverClient =
                    new com.proiectipdp.chess.client.ChessServerClient(
                            "http://localhost:8081"
                    );

            while (true) {

                try {
                    Thread.sleep(1000);

                    if (gameOverByTime[0]) {
                        break;
                    }

                    String gameJson = serverClient.getGameState(gameId);

                    if (gameJson == null ||
                            gameJson.equals("null") ||
                            gameJson.isBlank()) {

                        javafx.application.Platform.runLater(() -> {
                            finishLocalGame(
                                    boardView,
                                    timerRef,
                                    gameOverByTime,
                                    statusLabel,
                                    "Partida s-a terminat!",
                                    "#F2C14E"
                            );
                        });

                        break;
                    }

                    java.util.List<com.proiectipdp.chess.client.ChessServerClient.ServerMove> serverMoves =
                            serverClient.extractMovesFromGameJson(gameJson);

                    if (serverMoves.size() > syncedMoveCount[0]) {

                        java.util.List<com.proiectipdp.chess.client.ChessServerClient.ServerMove> movesToApply =
                                new java.util.ArrayList<>(
                                        serverMoves.subList(
                                                syncedMoveCount[0],
                                                serverMoves.size()
                                        )
                                );

                        javafx.application.Platform.runLater(() -> {

                            for (com.proiectipdp.chess.client.ChessServerClient.ServerMove remoteMove : movesToApply) {

                                boolean applied = boardView.applyRemoteMove(
                                        remoteMove.fromRow(),
                                        remoteMove.fromCol(),
                                        remoteMove.toRow(),
                                        remoteMove.toCol()
                                );

                                if (applied) {
                                    syncedMoveCount[0]++;

                                    if (refreshRef[0] != null) {
                                        refreshRef[0].run();
                                    }
                                } else {
                                    System.out.println(
                                            "Nu s-a putut aplica mutarea remote: " +
                                                    remoteMove.fromRow() + "," +
                                                    remoteMove.fromCol() + " -> " +
                                                    remoteMove.toRow() + "," +
                                                    remoteMove.toCol()
                                    );
                                }
                            }
                        });
                    }

                } catch (Exception ex) {
                    System.out.println("Polling oprit sau server indisponibil: " + ex.getMessage());

                    javafx.application.Platform.runLater(() -> {
                        finishLocalGame(
                                boardView,
                                timerRef,
                                gameOverByTime,
                                statusLabel,
                                "Conexiune pierdută / joc terminat",
                                "#F2C14E"
                        );
                    });

                    break;
                }
            }
        });

        pollingThread.setDaemon(true);
        pollingThread.start();
    }

    private void updateBoardInputState(BoardView boardView,
                                       GameState state,
                                       String playerColor,
                                       boolean gameOver) {

        if (gameOver) {
            boardView.setDisable(true);
            boardView.setMouseTransparent(true);
            return;
        }

        boolean myTurn =
                state.getTurn().name().equals(playerColor);

        boardView.setDisable(!myTurn);
        boardView.setMouseTransparent(!myTurn);
    }

    private void jumpToMove(GameState state, BoardView boardView, int targetMoveIndex) {
        while (state.getHistory().canPrev()) {
            state.loadSnapshot(state.getHistory().prev());
        }

        for (int i = 0; i < targetMoveIndex; i++) {
            if (state.getHistory().canNext()) {
                state.loadSnapshot(state.getHistory().next());
            }
        }

        boardView.deselectForExternal();
        boardView.forceRender();
    }

    private VBox clockCard(String player, Label clock) {
        Label playerLabel = new Label(player);
        playerLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #bfc7d0;");

        VBox box = new VBox(5, playerLabel, clock);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 12, 12, 12));
        box.setStyle(
                "-fx-background-color: rgba(255,255,255,0.06);" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: rgba(255,255,255,0.08);" +
                        "-fx-border-radius: 18;"
        );

        return box;
    }

    private void styleWhiteClock(Label label) {
        label.setMinWidth(115);
        label.setAlignment(Pos.CENTER);
        label.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ffffff, #e6e6e6);" +
                        "-fx-text-fill: #111111;" +
                        "-fx-font-size: 25px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 18 12 18;" +
                        "-fx-background-radius: 16;"
        );
    }

    private void styleBlackClock(Label label) {
        label.setMinWidth(115);
        label.setAlignment(Pos.CENTER);
        label.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #050505, #151515);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 25px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 18 12 18;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(255,255,255,0.12);" +
                        "-fx-border-radius: 16;"
        );
    }

    private void styleGoldButton(Button button) {
        String normal =
                "-fx-background-color: linear-gradient(to right, #f4d27a, #c9963e, #8e5c1d);" +
                        "-fx-background-radius: 14;" +
                        "-fx-text-fill: #111111;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 16 8 16;" +
                        "-fx-cursor: hand;";

        String hover =
                "-fx-background-color: linear-gradient(to right, #ffe79b, #d9a64d, #a66e25);" +
                        "-fx-background-radius: 14;" +
                        "-fx-text-fill: #111111;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 16 8 16;" +
                        "-fx-cursor: hand;";

        button.setStyle(normal);
        button.setOnMouseEntered(e -> button.setStyle(hover));
        button.setOnMouseExited(e -> button.setStyle(normal));
    }

    private void styleDarkButton(Button button) {
        String normal =
                "-fx-background-color: rgba(255,255,255,0.09);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(255,255,255,0.12);" +
                        "-fx-border-radius: 14;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 14 8 14;" +
                        "-fx-cursor: hand;";

        String hover =
                "-fx-background-color: rgba(244,210,122,0.20);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(244,210,122,0.55);" +
                        "-fx-border-radius: 14;" +
                        "-fx-text-fill: #f5d58b;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 14 8 14;" +
                        "-fx-cursor: hand;";

        button.setStyle(normal);
        button.setOnMouseEntered(e -> button.setStyle(hover));
        button.setOnMouseExited(e -> button.setStyle(normal));
    }

    private void styleNavButton(Button button) {
        button.setMinWidth(42);
        styleDarkButton(button);
    }

    private void styleCheckBox(CheckBox checkBox) {
        checkBox.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #f1f1f1;" +
                        "-fx-cursor: hand;" +
                        "-fx-mark-color: #f4d27a;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );
    }

    private DropShadow softShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.62));
        shadow.setRadius(28);
        shadow.setOffsetY(8);
        return shadow;
    }

    private String statusText(GameStatus s) {
        return switch (s) {
            case CHECK -> "ȘAH";
            case CHECKMATE -> "ȘAH-MAT";
            case STALEMATE -> "PAT";
            default -> "Partidă în desfășurare";
        };
    }

    private Color statusColor(GameStatus s) {
        return switch (s) {
            case CHECK, CHECKMATE -> Color.web("#F2C14E");
            case STALEMATE -> Color.web("#BBBBBB");
            default -> Color.web("#8d98a4");
        };
    }

    private String formatTime(int seconds) {
        if (seconds <= 0) {
            return "00:00";
        }

        int m = seconds / 60;
        int s = seconds % 60;

        return String.format("%02d:%02d", m, s);
    }


    public static void main(String[] args) {
        launch();
    }
}

