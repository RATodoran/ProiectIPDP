package com.proiectipdp.chess.core;

public record Position(int row, int col) {
    public boolean inBounds() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}