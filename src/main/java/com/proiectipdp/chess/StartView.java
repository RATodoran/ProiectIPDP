package com.proiectipdp.chess;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class StartView {

    private Scene scene;

    public StartView(MainApp mainApp) {

        // ===== STYLES =====
        String mainBackgroundStyle =
                "-fx-background-color: linear-gradient(to bottom right, #0d1117, #161b22, #252018);";

        String cardStyle =
                "-fx-background-color: rgba(18, 22, 27, 0.90);" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-color: rgba(218, 165, 75, 0.45);" +
                        "-fx-border-radius: 30;" +
                        "-fx-border-width: 1.3;" +
                        "-fx-padding: 45 60 45 60;";

        String titleStyle =
                "-fx-font-size: 42px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;";

        String subtitleStyle =
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #bfc7d0;";

        String primaryButtonStyle =
                "-fx-background-color: linear-gradient(to right, #f4d27a, #c9963e, #8e5c1d);" +
                        "-fx-background-radius: 16;" +
                        "-fx-text-fill: #111111;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 38 12 38;" +
                        "-fx-cursor: hand;";

        String primaryButtonHoverStyle =
                "-fx-background-color: linear-gradient(to right, #ffe79b, #d9a64d, #a66e25);" +
                        "-fx-background-radius: 16;" +
                        "-fx-text-fill: #111111;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 38 12 38;" +
                        "-fx-cursor: hand;";

        String secondaryButtonStyle =
                "-fx-background-color: rgba(255, 255, 255, 0.075);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(244, 210, 122, 0.55);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-text-fill: #f1f1f1;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 11 38 11 38;" +
                        "-fx-cursor: hand;";

        String secondaryButtonHoverStyle =
                "-fx-background-color: rgba(244, 210, 122, 0.16);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(255, 231, 155, 0.80);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-text-fill: #f4d27a;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 11 38 11 38;" +
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


        Circle glow1 = new Circle(150);
        glow1.setFill(Color.rgb(218, 165, 75, 0.16));
        glow1.setEffect(new GaussianBlur(65));

        Circle glow2 = new Circle(120);
        glow2.setFill(Color.rgb(80, 150, 190, 0.12));
        glow2.setEffect(new GaussianBlur(70));


        Label crown = new Label("♛");
        crown.setTextFill(Color.rgb(244, 210, 122, 0.95));
        crown.setStyle("-fx-font-size: 48px;");

        Label title = new Label("Chess Game");
        title.setStyle(titleStyle);

        Label subtitle = new Label("Pregătește partida și intră pe tablă");
        subtitle.setStyle(subtitleStyle);

        VBox titleBox = new VBox(4, crown, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);


        Button btnJoaca = new Button("Joacă!");
        Button btnLogin = new Button("Logare");

        btnJoaca.setPrefWidth(260);
        btnLogin.setPrefWidth(260);

        btnJoaca.setStyle(primaryButtonStyle);
        btnLogin.setStyle(secondaryButtonStyle);

        DropShadow buttonShadow = new DropShadow();
        buttonShadow.setColor(Color.rgb(244, 210, 122, 0.35));
        buttonShadow.setRadius(18);
        buttonShadow.setSpread(0.18);
        btnJoaca.setEffect(buttonShadow);

        btnJoaca.setOnMouseEntered(e -> btnJoaca.setStyle(primaryButtonHoverStyle));
        btnJoaca.setOnMouseExited(e -> btnJoaca.setStyle(primaryButtonStyle));

        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle(secondaryButtonHoverStyle));
        btnLogin.setOnMouseExited(e -> btnLogin.setStyle(secondaryButtonStyle));

        VBox buttonBox = new VBox(15, btnJoaca, btnLogin);
        buttonBox.setAlignment(Pos.CENTER);

        Label footer = new Label("♙  Choose your move  ♙");
        footer.setTextFill(Color.rgb(255, 255, 255, 0.35));
        footer.setStyle("-fx-font-size: 12px;");


        VBox card = new VBox(34, titleBox, buttonBox, footer);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(560);
        card.setStyle(cardStyle);

        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.65));
        cardShadow.setRadius(35);
        cardShadow.setOffsetY(10);
        card.setEffect(cardShadow);


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


        btnJoaca.setOnAction(e -> {
            GameSettingsView settings = new GameSettingsView(mainApp);
            mainApp.setScene(settings.getScene());
        });

        btnLogin.setOnAction(e -> {
            LoginView loginView = new LoginView(mainApp);
            mainApp.setScene(loginView.getScene());
        });
    }

    public Scene getScene() {
        return scene;
    }
}