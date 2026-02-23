package com.proiectipdp.chess.core;

public class Board {
    private final char[][] squares = new char[8][8];

    public Board() {
        clear();
    }

    public void clear() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                squares[r][c] = '#';
    }

    public char get(int row, int col) {
        return squares[row][col];
    }

    public void set(int row, int col, char piece) {
        squares[row][col] = piece;
    }

    public char[][] copySquares() {
        char[][] copy = new char[8][8];
        for (int r = 0; r < 8; r++) {
            System.arraycopy(squares[r], 0, copy[r], 0, 8);
        }
        return copy;
    }

    public Board copy() {
        Board b = new Board();
        char[][] copy = copySquares();
        for (int r = 0; r < 8; r++) {
            System.arraycopy(copy[r], 0, b.squares[r], 0, 8);
        }
        return b;
    }
}