
package com.proiectipdp.chess;
import com.proiectipdp.chess.MainApp;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;


public class StartView {

    private Scene scene;

    public StartView(MainApp mainApp) {

        Button btnJoaca = new Button("Joaca!");
        Button btnLogin = new Button("Logare");

        btnJoaca.setPrefWidth(200);
        btnLogin.setPrefWidth(200);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2f2f2f;");

        layout.getChildren().addAll(btnJoaca, btnLogin);

        scene = new Scene(layout, 800, 600);


        btnJoaca.setOnAction(e -> {
            GameSettingsView settings = new GameSettingsView(mainApp);
            mainApp.setScene(settings.getScene());
        });

        btnLogin.setOnAction(e -> {
            System.out.println("Login...");
        });
    }

    public Scene getScene() {
        return scene;
    }
}