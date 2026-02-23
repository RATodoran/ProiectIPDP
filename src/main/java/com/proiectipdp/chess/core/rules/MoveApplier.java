package com.proiectipdp.chess.core.rules;

import com.proiectipdp.chess.core.Color;
import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Move;
import com.proiectipdp.chess.core.Position;

public class MoveApplier {

    public void apply(GameState state, Move move) {
        Position f = move.from();
        Position t = move.to();

        char piece = state.getBoard().get(f.row(), f.col());
        char dest  = state.getBoard().get(t.row(), t.col());

        // reset en passant by default; will set again on pawn double
        state.setEnPassantTarget(null);

        boolean isPawn = (RulesUtil.toLowerPiece(piece) == 'p');
        boolean isKing = (RulesUtil.toLowerPiece(piece) == 'k');
        boolean isRook = (RulesUtil.toLowerPiece(piece) == 'r');

        // ---- en passant capture ----
        if (isPawn && RulesUtil.isEmpty(dest)) {
            Position ep = state.getEnPassantTarget(); // careful: we cleared it above
            // We need previous EP, so read before clearing:
        }

        // We need previous EP, so let's recompute: store before clearing
    }

    public void applyWithPrevEp(GameState state, Move move) {
        Position prevEp = state.getEnPassantTarget();
        // now apply using prevEp
        doApply(state, move, prevEp);
    }

    private void doApply(GameState state, Move move, Position prevEp) {
        Position f = move.from();
        Position t = move.to();

        char piece = state.getBoard().get(f.row(), f.col());
        char dest  = state.getBoard().get(t.row(), t.col());

        boolean isPawn = (RulesUtil.toLowerPiece(piece) == 'p');
        boolean isKing = (RulesUtil.toLowerPiece(piece) == 'k');
        boolean isRook = (RulesUtil.toLowerPiece(piece) == 'r');

        // update castling rights if rook captured on corner
        if (!RulesUtil.isEmpty(dest) && RulesUtil.toLowerPiece(dest) == 'r') {
            // if a rook on a corner is captured, remove that side right
            if (t.row() == 7 && t.col() == 0) state.setCastleWQ(false);
            if (t.row() == 7 && t.col() == 7) state.setCastleWK(false);
            if (t.row() == 0 && t.col() == 0) state.setCastleBQ(false);
            if (t.row() == 0 && t.col() == 7) state.setCastleBK(false);
        }

        // clear en passant by default
        state.setEnPassantTarget(null);

        // ----- CASTLING apply -----
        if (isKing && Math.abs(t.col() - f.col()) == 2 && t.row() == f.row()) {
            // move king
            state.getBoard().set(t.row(), t.col(), piece);
            state.getBoard().set(f.row(), f.col(), '#');

            // move rook
            if (t.col() == 6) { // king side
                char rook = state.getBoard().get(f.row(), 7);
                state.getBoard().set(f.row(), 5, rook);
                state.getBoard().set(f.row(), 7, '#');
            } else if (t.col() == 2) { // queen side
                char rook = state.getBoard().get(f.row(), 0);
                state.getBoard().set(f.row(), 3, rook);
                state.getBoard().set(f.row(), 0, '#');
            }

            // after king move, lose both rights for that color
            if (RulesUtil.isWhite(piece)) {
                state.setCastleWK(false);
                state.setCastleWQ(false);
            } else {
                state.setCastleBK(false);
                state.setCastleBQ(false);
            }

            // switch turn
            state.setTurn(state.getTurn().opposite());
            return;
        }

        // ----- EN PASSANT apply -----
        if (isPawn && RulesUtil.isEmpty(dest) && prevEp != null
                && prevEp.row() == t.row() && prevEp.col() == t.col()
                && Math.abs(t.col() - f.col()) == 1) {

            // move pawn
            state.getBoard().set(t.row(), t.col(), piece);
            state.getBoard().set(f.row(), f.col(), '#');

            // remove captured pawn behind target square
            if (RulesUtil.isWhite(piece)) {
                state.getBoard().set(t.row() + 1, t.col(), '#');
            } else {
                state.getBoard().set(t.row() - 1, t.col(), '#');
            }

            // promotion?
            handlePromotionIfNeeded(state, move, piece, t);

            state.setTurn(state.getTurn().opposite());
            return;
        }

        // ----- NORMAL move/capture -----
        state.getBoard().set(t.row(), t.col(), piece);
        state.getBoard().set(f.row(), f.col(), '#');

        // update castling rights when king/rook moves
        if (isKing) {
            if (RulesUtil.isWhite(piece)) { state.setCastleWK(false); state.setCastleWQ(false); }
            else { state.setCastleBK(false); state.setCastleBQ(false); }
        }
        if (isRook) {
            // rook moved from corner => lose that side right
            if (f.row() == 7 && f.col() == 0) state.setCastleWQ(false);
            if (f.row() == 7 && f.col() == 7) state.setCastleWK(false);
            if (f.row() == 0 && f.col() == 0) state.setCastleBQ(false);
            if (f.row() == 0 && f.col() == 7) state.setCastleBK(false);
        }

        // set en passant target if pawn double move
        if (isPawn && Math.abs(t.row() - f.row()) == 2) {
            int midRow = (t.row() + f.row()) / 2;
            state.setEnPassantTarget(new Position(midRow, f.col()));
        }

        // promotion?
        handlePromotionIfNeeded(state, move, piece, t);

        state.setTurn(state.getTurn().opposite());
    }

    private void handlePromotionIfNeeded(GameState state, Move move, char movedPiece, Position to) {
        if (RulesUtil.toLowerPiece(movedPiece) != 'p') return;

        boolean white = RulesUtil.isWhite(movedPiece);
        int promoRow = white ? 0 : 7;

        if (to.row() != promoRow) return;

        Character promo = move.promotion();
        char promoteTo;

        if (promo == null) {
            promoteTo = white ? 'Q' : 'q';
        } else {
            char p = Character.toUpperCase(promo);
            if (p != 'Q' && p != 'R' && p != 'B' && p != 'N') p = 'Q';
            promoteTo = white ? p : Character.toLowerCase(p);
        }

        state.getBoard().set(to.row(), to.col(), promoteTo);
    }
}