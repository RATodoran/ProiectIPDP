package com.proiectipdp.chess.core.rules.pieces;

import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Move;
import com.proiectipdp.chess.core.Position;
import com.proiectipdp.chess.core.rules.RulesUtil;

public class PawnRules implements PieceRules {

    @Override
    public boolean isPseudoLegal(GameState state, Move move) {
        Position f = move.from();
        Position t = move.to();
        char pawn = state.getBoard().get(f.row(), f.col());

        boolean white = RulesUtil.isWhite(pawn);
        int dir = white ? -1 : 1;

        int dr = t.row() - f.row();
        int dc = t.col() - f.col();

        char dest = state.getBoard().get(t.row(), t.col());

        // forward 1
        if (dc == 0 && dr == dir) {
            return RulesUtil.isEmpty(dest);
        }

        // forward 2 from start rank
        int startRow = white ? 6 : 1;
        if (dc == 0 && f.row() == startRow && dr == 2 * dir) {
            int midRow = f.row() + dir;
            return RulesUtil.isEmpty(state.getBoard().get(midRow, f.col())) && RulesUtil.isEmpty(dest);
        }

        // capture diagonal
        if (Math.abs(dc) == 1 && dr == dir) {
            // normal capture
            if (!RulesUtil.isEmpty(dest) && RulesUtil.isOpponent(pawn, dest)) return true;

            // en passant capture (dest is empty, but target matches)
            Position ep = state.getEnPassantTarget();
            return ep != null && ep.row() == t.row() && ep.col() == t.col();
        }

        return false;
    }
}