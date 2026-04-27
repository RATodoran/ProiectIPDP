package com.proiectipdp.chess.ui;

public class MoveRow {

    private final int number;
    private final String white;
    private final String black;

    public MoveRow(int number, String white, String black) {
        this.number = number;
        this.white = white;
        this.black = black;
    }

    public int getNumber() {
        return number;
    }

    public String getWhite() {
        return white;
    }

    public String getBlack() {
        return black;
    }
}