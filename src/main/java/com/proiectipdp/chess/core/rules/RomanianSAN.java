package com.proiectipdp.chess.core.rules;

import com.proiectipdp.chess.core.Color;
import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Move;
import com.proiectipdp.chess.core.Position;

public final class RomanianSAN {

    private RomanianSAN() {}

    public static String toSAN(GameState stateBefore, Move move, MoveValidator validator) {
        Position f = move.from();
        Position t = move.to();

        char piece = stateBefore.getBoard().get(f.row(), f.col());
        char destPiece = stateBefore.getBoard().get(t.row(), t.col());

        // 1) Rocadă
        if (RulesUtil.toLowerPiece(piece) == 'k' && f.row() == t.row() && Math.abs(t.col() - f.col()) == 2) {
            return (t.col() == 6) ? "O-O" : "O-O-O";
        }

        boolean isPawn = RulesUtil.toLowerPiece(piece) == 'p';

        boolean isEnPassant = isPawn
                && RulesUtil.isEmpty(destPiece)
                && stateBefore.getEnPassantTarget() != null
                && stateBefore.getEnPassantTarget().row() == t.row()
                && stateBefore.getEnPassantTarget().col() == t.col()
                && Math.abs(t.col() - f.col()) == 1;

        boolean isCapture = (!RulesUtil.isEmpty(destPiece) && RulesUtil.isOpponent(piece, destPiece)) || isEnPassant;

        String destSquare = toSquare(t);

        // 2) Pion
        if (isPawn) {
            String s;
            if (isCapture) {
                // ex: exd5, exf4 (file de plecare + x + dest)
                s = "" + fileChar(f.col()) + "x" + destSquare;
            } else {
                // ex: e4
                s = destSquare;
            }

            // promotion (dacă există)
            if (move.promotion() != null) {
                s += "=" + pieceLetterFromPromotion(move.promotion());
            }

            return s;
        }

        // 3) Piesă (T/C/N/D/R)
        String pieceLetter = pieceLetter(piece);
        String disambig = disambiguation(stateBefore, move, validator);
        String captureMark = isCapture ? "x" : "";

        return pieceLetter + disambig + captureMark + destSquare;
    }

    /**
     * Returnează: "", "a", "1" sau "a1" (coloana prioritar, apoi linia)
     */
    private static String disambiguation(GameState state, Move move, MoveValidator validator) {
        Position f = move.from();
        Position t = move.to();
        char piece = state.getBoard().get(f.row(), f.col());
        Color mover = state.getTurn();

        boolean anyOther = false;
        boolean sameFileExists = false;
        boolean sameRankExists = false;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (r == f.row() && c == f.col()) continue;

                char p = state.getBoard().get(r, c);
                if (RulesUtil.isEmpty(p)) continue;
                if (RulesUtil.colorOf(p) != mover) continue;

                // aceeași piesă (tip), ex: doi cai, două ture etc.
                if (RulesUtil.toLowerPiece(p) != RulesUtil.toLowerPiece(piece)) continue;

                Move alt = new Move(new Position(r, c), t, move.promotion());

                // Trebuie să fie mutare legală (SAN folosește ambiguitatea între mutări legale)
                if (validator.isLegal(state, alt)) {
                    anyOther = true;
                    if (c == f.col()) sameFileExists = true;
                    if (r == f.row()) sameRankExists = true;
                }
            }
        }

        if (!anyOther) return "";

        // cerința ta: coloana prioritar, apoi linia
        // - dacă altă piesă poate ajunge și are aceeași coloană ca piesa curentă -> trebuie și linie
        // - altfel, coloana e suficientă
        if (!sameFileExists) {
            return "" + fileChar(f.col());
        }

        // dacă există în aceeași coloană, coloana singură nu ajută; încearcă linia
        if (!sameRankExists) {
            return "" + rankNum(f.row());
        }

        // worst case: aceeași coloană și aceeași linie (rar) -> ambele
        return "" + fileChar(f.col()) + rankNum(f.row());
    }

    private static String pieceLetter(char piece) {
        return switch (RulesUtil.toLowerPiece(piece)) {
            case 'r' -> "T"; // Turn
            case 'n' -> "C"; // Cal
            case 'b' -> "N"; // Nebun
            case 'q' -> "D"; // Damă (regină)
            case 'k' -> "R"; // Rege
            default -> "";
        };
    }

    private static String pieceLetterFromPromotion(char promo) {
        // promo e 'Q'/'R'/'B'/'N' sau similar
        return switch (Character.toUpperCase(promo)) {
            case 'Q' -> "D";
            case 'R' -> "T";
            case 'B' -> "N";
            case 'N' -> "C";
            default -> "D";
        };
    }

    private static String toSquare(Position p) {
        return "" + fileChar(p.col()) + rankNum(p.row());
    }

    private static char fileChar(int col) {
        return (char) ('a' + col);
    }

    private static int rankNum(int row) {
        return 8 - row;
    }
}