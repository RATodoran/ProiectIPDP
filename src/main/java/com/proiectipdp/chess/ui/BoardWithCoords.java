package com.proiectipdp.chess.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class BoardWithCoords extends GridPane {

    private final BoardView boardView;

    public BoardWithCoords(BoardView boardView) {
        this.boardView = boardView;

        build();
    }

    private void build() {

        // adăugăm tabla pe poziția (1,0)
        add(boardView, 1, 0, 8, 8);

        // 🔢 NUMERE (stânga)
        for (int row = 0; row < 8; row++) {
            Label num = new Label(getNumber(row));
            num.setPrefHeight(72);
            num.setMinWidth(20);
            num.setAlignment(Pos.CENTER);

            add(num, 0, row);
        }

        // 🔤 LITERE (jos)
        for (int col = 0; col < 8; col++) {
            Label let = new Label(getLetter(col));
            let.setPrefWidth(72);
            let.setAlignment(Pos.CENTER);

            add(let, col + 1, 8);
        }
    }

    private String getLetter(int col) {
        return boardView.isFlipped()
                ? String.valueOf((char) ('h' - col))
                : String.valueOf((char) ('a' + col));
    }

    private String getNumber(int row) {
        return boardView.isFlipped()
                ? String.valueOf(row + 1)
                : String.valueOf(8 - row);
    }

    public void refreshCoords() {
        getChildren().clear();
        build();
    }
}