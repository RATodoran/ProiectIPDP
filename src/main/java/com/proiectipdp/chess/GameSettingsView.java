package com.proiectipdp.chess;
import com.proiectipdp.chess.client.ChessServerClient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameSettingsView {

    private Scene scene;

    private int selectedTime = 10; // default
    private String selectedColor = "WHITE"; // default


    public GameSettingsView(MainApp mainApp) {


        String mainBackgroundStyle =
                "-fx-background-color: linear-gradient(to bottom right, #0d1117, #161b22, #252018);";

        String cardStyle =
                "-fx-background-color: rgba(18, 22, 27, 0.90);" +
                        "-fx-background-radius: 28;" +
                        "-fx-border-color: rgba(218, 165, 75, 0.45);" +
                        "-fx-border-radius: 28;" +
                        "-fx-border-width: 1.3;" +
                        "-fx-padding: 38 48 40 48;";

        String innerPanelStyle =
                "-fx-background-color: rgba(255, 255, 255, 0.055);" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.08);" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 18 22 18 22;";

        String titleStyle =
                "-fx-font-size: 34px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;";

        String subtitleStyle =
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #bfc7d0;";

        String sectionTitleStyle =
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #f5d58b;";

        String smallInfoStyle =
                "-fx-font-size: 11px;" +
                        "-fx-text-fill: #8d98a4;";

        String buttonStyle =
                "-fx-background-color: linear-gradient(to right, #f4d27a, #c9963e, #8e5c1d);" +
                        "-fx-background-radius: 16;" +
                        "-fx-text-fill: #111111;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 38 12 38;" +
                        "-fx-cursor: hand;";

        String buttonHoverStyle =
                "-fx-background-color: linear-gradient(to right, #ffe79b, #d9a64d, #a66e25);" +
                        "-fx-background-radius: 16;" +
                        "-fx-text-fill: #111111;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 38 12 38;" +
                        "-fx-cursor: hand;";


        Label topPieces = new Label("♔     ♕     ♖     ♘     ♗     ♙");
        topPieces.setTextFill(Color.rgb(255, 255, 255, 0.09));
        topPieces.setStyle("-fx-font-size: 52px;");

        Label bottomPieces = new Label("♟     ♝     ♞     ♜     ♛     ♚");
        bottomPieces.setTextFill(Color.rgb(218, 165, 75, 0.10));
        bottomPieces.setStyle("-fx-font-size: 48px;");

        Label leftPiece = new Label("♞");
        leftPiece.setTextFill(Color.rgb(255, 255, 255, 0.055));
        leftPiece.setStyle("-fx-font-size: 190px;");

        Label rightPiece = new Label("♜");
        rightPiece.setTextFill(Color.rgb(218, 165, 75, 0.07));
        rightPiece.setStyle("-fx-font-size: 190px;");

        // ===== GLOW CIRCLES =====
        Circle glow1 = new Circle(150);
        glow1.setFill(Color.rgb(218, 165, 75, 0.16));
        glow1.setEffect(new GaussianBlur(65));

        Circle glow2 = new Circle(120);
        glow2.setFill(Color.rgb(80, 150, 190, 0.12));
        glow2.setEffect(new GaussianBlur(70));

        // ===== TITLE =====
        Label title = new Label("Game Settings");
        title.setStyle(titleStyle);

        Label subtitle = new Label("Alege timpul, culoarea și pregătește tabla");
        subtitle.setStyle(subtitleStyle);

        Label crown = new Label("♛");
        crown.setTextFill(Color.rgb(244, 210, 122, 0.95));
        crown.setStyle("-fx-font-size: 40px;");

        VBox titleBox = new VBox(3, crown, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);

        // ===== TIME CONTROL =====
        Label timeLabel = new Label("Timp de joc");
        timeLabel.setStyle(sectionTitleStyle);

        Label timeInfo = new Label("Selectează durata pentru fiecare jucător");
        timeInfo.setStyle(smallInfoStyle);

        ToggleGroup timeGroup = new ToggleGroup();

        RadioButton t3 = new RadioButton("3 min");
        RadioButton t5 = new RadioButton("5 min");
        RadioButton t10 = new RadioButton("10 min");
        RadioButton t15 = new RadioButton("15 min");
        RadioButton t30 = new RadioButton("30 min");

        styleRadioButton(t3);
        styleRadioButton(t5);
        styleRadioButton(t10);
        styleRadioButton(t15);
        styleRadioButton(t30);

        t3.setToggleGroup(timeGroup);
        t5.setToggleGroup(timeGroup);
        t10.setToggleGroup(timeGroup);
        t15.setToggleGroup(timeGroup);
        t30.setToggleGroup(timeGroup);

        t10.setSelected(true);

        timeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == t3) selectedTime = 3;
            if (newVal == t5) selectedTime = 5;
            if (newVal == t10) selectedTime = 10;
            if (newVal == t15) selectedTime = 15;
            if (newVal == t30) selectedTime = 30;
        });

        VBox timeOptions = new VBox(8, t3, t5, t10, t15, t30);
        timeOptions.setAlignment(Pos.CENTER_LEFT);

        Separator timeSeparator = new Separator();
        timeSeparator.setStyle("-fx-background-color: rgba(255, 255, 255, 0.14);");

        VBox timePanel = new VBox(5, timeLabel, timeInfo, timeSeparator, timeOptions);
        timePanel.setAlignment(Pos.CENTER_LEFT);
        timePanel.setPrefWidth(215);
        timePanel.setStyle(innerPanelStyle);

        // ===== COLOR CONTROL =====
        Label colorLabel = new Label("Culoare");
        colorLabel.setStyle(sectionTitleStyle);

        Label colorInfo = new Label("Alege partea cu care începi partida");
        colorInfo.setStyle(smallInfoStyle);

        ToggleGroup colorGroup = new ToggleGroup();

        RadioButton white = new RadioButton("Alb");
        RadioButton black = new RadioButton("Negru");
        RadioButton random = new RadioButton("Random");

        styleRadioButton(white);
        styleRadioButton(black);
        styleRadioButton(random);

        white.setToggleGroup(colorGroup);
        black.setToggleGroup(colorGroup);
        random.setToggleGroup(colorGroup);

        white.setSelected(true);

        colorGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == white) selectedColor = "WHITE";
            if (newVal == black) selectedColor = "BLACK";
            if (newVal == random) selectedColor = "RANDOM";
        });

        VBox colorOptions = new VBox(8, white, black, random);
        colorOptions.setAlignment(Pos.CENTER_LEFT);

        Separator colorSeparator = new Separator();
        colorSeparator.setStyle("-fx-background-color: rgba(255, 255, 255, 0.14);");

        VBox colorPanel = new VBox(5, colorLabel, colorInfo, colorSeparator, colorOptions);
        colorPanel.setAlignment(Pos.CENTER_LEFT);
        colorPanel.setPrefWidth(215);
        colorPanel.setStyle(innerPanelStyle);

        // ===== OPTIONS BOX =====
        HBox optionsBox = new HBox(22, timePanel, colorPanel);
        optionsBox.setAlignment(Pos.CENTER);

        // ===== START BUTTON =====
        Button startBtn = new Button("Start Game");
        startBtn.setPrefWidth(260);
        startBtn.setStyle(buttonStyle);

        DropShadow buttonShadow = new DropShadow();
        buttonShadow.setColor(Color.rgb(244, 210, 122, 0.35));
        buttonShadow.setRadius(18);
        buttonShadow.setSpread(0.18);
        startBtn.setEffect(buttonShadow);

        startBtn.setOnMouseEntered(e -> startBtn.setStyle(buttonHoverStyle));
        startBtn.setOnMouseExited(e -> startBtn.setStyle(buttonStyle));

        startBtn.setOnAction(e -> {
            startBtn.setDisable(true);
            startBtn.setText("Se caută adversar...");

            new Thread(() -> {
                try {

                    ChessServerClient serverClient =
                            new ChessServerClient("http://192.168.0.105:8081");

                    String response = serverClient.joinGame("Razvan");

                    System.out.println("Răspuns server: " + response);

                    javafx.application.Platform.runLater(() -> {

                        if (response.startsWith("Waiting")) {
                            startBtn.setDisable(false);
                            startBtn.setText("Se caută adversar...");

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Matchmaking");
                            alert.setHeaderText(null);
                            alert.setContentText("Cererea a fost trimisă. Așteaptă adversar.");
                            alert.showAndWait();

                            return;
                        }

                        if (response.startsWith("Game started")) {
                            mainApp.showGame(selectedTime, selectedColor);
                            return;
                        }

                        startBtn.setDisable(false);
                        startBtn.setText("Start Game");

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Răspuns necunoscut");
                        alert.setHeaderText("Serverul a răspuns neașteptat.");
                        alert.setContentText(response);
                        alert.showAndWait();
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();

                    javafx.application.Platform.runLater(() -> {
                        startBtn.setDisable(false);
                        startBtn.setText("Start Game");

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Eroare server");
                        alert.setHeaderText("Nu s-a putut trimite cererea de joc.");
                        alert.setContentText(ex.getMessage());
                        alert.showAndWait();
                    });
                }
            }).start();
        });
        Label footer = new Label("♙  Ready for battle  ♙");
        footer.setTextFill(Color.rgb(255, 255, 255, 0.35));
        footer.setStyle("-fx-font-size: 12px;");

        // ===== CARD =====
        VBox card = new VBox(28, titleBox, optionsBox, startBtn, footer);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(610);
        card.setStyle(cardStyle);

        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.65));
        cardShadow.setRadius(35);
        cardShadow.setOffsetY(10);
        card.setEffect(cardShadow);

        // ===== MAIN CONTENT =====
        VBox centerContent = new VBox(18, topPieces, card, bottomPieces);
        centerContent.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setStyle(mainBackgroundStyle);
        root.setPadding(new Insets(25));

        root.setCenter(centerContent);
        root.setLeft(leftPiece);
        root.setRight(rightPiece);

        BorderPane.setAlignment(leftPiece, Pos.CENTER_LEFT);
        BorderPane.setAlignment(rightPiece, Pos.CENTER_RIGHT);

        // ===== STACK BACKGROUND + CONTENT =====
        StackPane stack = new StackPane();

        StackPane.setAlignment(glow1, Pos.TOP_LEFT);
        StackPane.setMargin(glow1, new Insets(45, 0, 0, 80));

        StackPane.setAlignment(glow2, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(glow2, new Insets(0, 90, 55, 0));

        stack.getChildren().addAll(glow1, glow2, root);

        scene = new Scene(stack, 900, 650);
    }

    public Scene getScene() {
        return scene;
    }

    private void styleRadioButton(RadioButton radioButton) {
        String normalStyle =
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #f1f1f1;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 0 6 0;" +
                        "-fx-mark-color: #f4d27a;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;";

        String hoverStyle =
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #f4d27a;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 0 6 0;" +
                        "-fx-mark-color: #ffe79b;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;";

        radioButton.setStyle(normalStyle);

        radioButton.setOnMouseEntered(e -> radioButton.setStyle(hoverStyle));
        radioButton.setOnMouseExited(e -> radioButton.setStyle(normalStyle));
    }
}