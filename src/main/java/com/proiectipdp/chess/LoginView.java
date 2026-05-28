package com.proiectipdp.chess;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class LoginView {

    private Scene scene;

    public LoginView(MainApp mainApp) {


        String mainBackgroundStyle =
                "-fx-background-color: linear-gradient(to bottom right, #0d1117, #161b22, #252018);";

        String cardStyle =
                "-fx-background-color: rgba(18, 22, 27, 0.92);" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-color: rgba(218, 165, 75, 0.45);" +
                        "-fx-border-radius: 30;" +
                        "-fx-border-width: 1.3;" +
                        "-fx-padding: 45 60 45 60;";

        String titleStyle =
                "-fx-font-size: 38px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;";

        String subtitleStyle =
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #bfc7d0;";

        String fieldStyle =
                "-fx-background-color: rgba(255, 255, 255, 0.08);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(244, 210, 122, 0.35);" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #8d98a4;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 11 14 11 14;";

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
        crown.setStyle("-fx-font-size: 46px;");

        Label title = new Label("Logare");
        title.setStyle(titleStyle);

        Label subtitle = new Label("Intră în cont pentru a continua");
        subtitle.setStyle(subtitleStyle);

        VBox titleBox = new VBox(4, crown, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);


        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(300);
        usernameField.setStyle(fieldStyle);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Parolă");
        passwordField.setPrefWidth(300);
        passwordField.setStyle(fieldStyle);

        Label messageLabel = new Label("");
        messageLabel.setTextFill(Color.rgb(244, 210, 122, 0.95));
        messageLabel.setStyle("-fx-font-size: 12px;");

        VBox formBox = new VBox(14, usernameField, passwordField, messageLabel);
        formBox.setAlignment(Pos.CENTER);


        Button loginBtn = new Button("Login și continuă");
        Button backBtn = new Button("Înapoi");

        loginBtn.setPrefWidth(260);
        backBtn.setPrefWidth(260);

        loginBtn.setStyle(primaryButtonStyle);
        backBtn.setStyle(secondaryButtonStyle);

        DropShadow buttonShadow = new DropShadow();
        buttonShadow.setColor(Color.rgb(244, 210, 122, 0.35));
        buttonShadow.setRadius(18);
        buttonShadow.setSpread(0.18);
        loginBtn.setEffect(buttonShadow);

        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(primaryButtonHoverStyle));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(primaryButtonStyle));

        backBtn.setOnMouseEntered(e -> backBtn.setStyle(secondaryButtonHoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(secondaryButtonStyle));

        VBox buttonBox = new VBox(14, loginBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        Label footer = new Label("♙  Welcome back, player  ♙");
        footer.setTextFill(Color.rgb(255, 255, 255, 0.35));
        footer.setStyle("-fx-font-size: 12px;");


        VBox card = new VBox(30, titleBox, formBox, buttonBox, footer);
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


        StackPane stack = new StackPane();

        StackPane.setAlignment(glow1, Pos.TOP_LEFT);
        StackPane.setMargin(glow1, new Insets(45, 0, 0, 80));

        StackPane.setAlignment(glow2, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(glow2, new Insets(0, 90, 55, 0));

        stack.getChildren().addAll(glow1, glow2, root);

        scene = new Scene(stack, 900, 650);


        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Completează username-ul și parola.");
            } else {
                System.out.println("Login pentru: " + username);

                GameSettingsView settings = new GameSettingsView(mainApp);
                mainApp.setScene(settings.getScene());
            }
        });

        backBtn.setOnAction(e -> {
            StartView startView = new StartView(mainApp);
            mainApp.setScene(startView.getScene());
        });
    }

    public Scene getScene() {
        return scene;
    }
}