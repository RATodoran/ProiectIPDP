package com.proiectipdp.chess.core.rules;

import com.proiectipdp.chess.core.Color;
import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Move;
import com.proiectipdp.chess.core.Position;
import com.proiectipdp.chess.core.rules.pieces.PieceRules;

public class MoveValidator {

    private final PieceMoveResolver resolver = new PieceMoveResolver();
    private final AttackMap attackMap = new AttackMap();
    private final MoveApplier applier = new MoveApplier();

    public boolean isPseudoLegal(GameState state, Move move) {
        Position f = move.from();
        Position t = move.to();
        if (f == null || t == null) return false;
        if (!f.inBounds() || !t.inBounds()) return false;
        if (f.row() == t.row() && f.col() == t.col()) return false;

        char piece = state.getBoard().get(f.row(), f.col());
        if (RulesUtil.isEmpty(piece)) return false;

        // turn
        Color pc = RulesUtil.colorOf(piece);
        if (pc != state.getTurn()) return false;

        char dest = state.getBoard().get(t.row(), t.col());

        // can't capture own
        if (RulesUtil.sameColor(piece, dest)) return false;

        PieceRules rules = resolver.rulesFor(piece);
        if (rules == null) return false;

        return rules.isPseudoLegal(state, move);
    }

    public boolean isLegal(GameState state, Move move) {
        if (!isPseudoLegal(state, move)) return false;

        // Extra castling constraints: king not in check, and squares crossed not attacked
        if (isCastlingMove(state, move)) {
            Color side = state.getTurn();

            Position kingPos = new Position(move.from().row(), move.from().col());
            // king currently in check?
            if (attackMap.isSquareAttacked(state, kingPos, side.opposite())) return false;

            int row = move.from().row();
            int fromC = move.from().col();
            int toC = move.to().col();
            int step = (toC > fromC) ? 1 : -1;

            // king passes through one square and ends on another -> both must not be attacked
            Position mid = new Position(row, fromC + step);
            Position end = new Position(row, toC);
            if (attackMap.isSquareAttacked(state, mid, side.opposite())) return false;
            if (attackMap.isSquareAttacked(state, end, side.opposite())) return false;
        }

        // simulate move and ensure your king not left in check
        GameState copy = state.copy();
        // IMPORTANT: use applyWithPrevEp to properly use previous ep target
        applier.applyWithPrevEp(copy, move);

        Color mover = state.getTurn(); // before switch
        Position king = attackMap.findKing(copy, mover);
        if (king == null) return false;

        return !attackMap.isSquareAttacked(copy, king, mover.opposite());
    }

    private boolean isCastlingMove(GameState state, Move move) {
        Position f = move.from();
        Position t = move.to();
        char piece = state.getBoard().get(f.row(), f.col());
        if (RulesUtil.toLowerPiece(piece) != 'k') return false;
        return f.row() == t.row() && Math.abs(t.col() - f.col()) == 2;
    }
}