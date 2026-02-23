package com.proiectipdp.chess.core.rules;

import com.proiectipdp.chess.core.Color;

public final class RulesUtil {

    private RulesUtil() {}

    public static boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    public static boolean isEmpty(char p) {
        return p == '#';
    }

    public static boolean isWhite(char p) {
        return p >= 'A' && p <= 'Z';
    }

    public static boolean isBlack(char p) {
        return p >= 'a' && p <= 'z';
    }

    public static Color colorOf(char p) {
        if (isWhite(p)) return Color.WHITE;
        if (isBlack(p)) return Color.BLACK;
        return null;
    }

    public static boolean sameColor(char a, char b) {
        if (isEmpty(a) || isEmpty(b)) return false;
        return (isWhite(a) && isWhite(b)) || (isBlack(a) && isBlack(b));
    }

    public static boolean isOpponent(char a, char b) {
        if (isEmpty(a) || isEmpty(b)) return false;
        return !sameColor(a, b);
    }

    public static char toLowerPiece(char p) {
        if (p >= 'A' && p <= 'Z') return (char)(p - 'A' + 'a');
        return p;
    }
}