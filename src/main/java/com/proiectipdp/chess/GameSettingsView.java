package com.proiectipdp.chess;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class GameSettingsView {

    private Scene scene;

    private int selectedTime = 10; // default
    private String selectedColor = "WHITE"; // default

    public GameSettingsView(MainApp mainApp) {

        String textStyle = "-fx-text-fill: white;";

        // ===== TITLE =====
        Label title = new Label("Game Settings");
        title.setStyle("-fx-font-size:24px; -fx-text-fill:white;");

        // ===== TIME CONTROL =====
        Label timeLabel = new Label("Alege timpul:");
        timeLabel.setStyle(textStyle);

        ToggleGroup timeGroup = new ToggleGroup();

        RadioButton t3 = new RadioButton("3 min");
        RadioButton t5 = new RadioButton("5 min");
        RadioButton t10 = new RadioButton("10 min");
        RadioButton t15 = new RadioButton("15 min");
        RadioButton t30 = new RadioButton("30 min");

        // 🔥 IMPORTANT: culoare text
        t3.setStyle(textStyle);
        t5.setStyle(textStyle);
        t10.setStyle(textStyle);
        t15.setStyle(textStyle);
        t30.setStyle(textStyle);

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

        VBox timeBox = new VBox(5, t3, t5, t10, t15, t30);
        timeBox.setAlignment(Pos.CENTER);

        // ===== COLOR =====
        Label colorLabel = new Label("Alege culoarea:");
        colorLabel.setStyle(textStyle);

        ToggleGroup colorGroup = new ToggleGroup();

        RadioButton white = new RadioButton("Alb");
        RadioButton black = new RadioButton("Negru");
        RadioButton random = new RadioButton("Random");

        // 🔥 IMPORTANT: culoare text
        white.setStyle(textStyle);
        black.setStyle(textStyle);
        random.setStyle(textStyle);

        white.setToggleGroup(colorGroup);
        black.setToggleGroup(colorGroup);
        random.setToggleGroup(colorGroup);

        white.setSelected(true);

        colorGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == white) selectedColor = "WHITE";
            if (newVal == black) selectedColor = "BLACK";
            if (newVal == random) selectedColor = "RANDOM";
        });

        VBox colorBox = new VBox(5, white, black, random);
        colorBox.setAlignment(Pos.CENTER);

        // ===== BUTTON =====
        Button startBtn = new Button("Start Game");
        startBtn.setPrefWidth(200);

        startBtn.setOnAction(e -> {
            mainApp.showGame(selectedTime, selectedColor);
        });

        // ===== LAYOUT =====
        VBox layout = new VBox(
                20,
                title,
                timeLabel,
                timeBox,
                colorLabel,
                colorBox,
                startBtn
        );

        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color:#2f2f2f;");

        scene = new Scene(layout, 800, 600);
    }

    public Scene getScene() {
        return scene;
    }
}