package com.proiectipdp.chess.core.rules.pieces;

import com.proiectipdp.chess.core.Color;
import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Move;
import com.proiectipdp.chess.core.Position;
import com.proiectipdp.chess.core.rules.RulesUtil;

public class KingRules implements PieceRules {

    @Override
    public boolean isPseudoLegal(GameState state, Move move) {
        Position f = move.from();
        Position t = move.to();
        char king = state.getBoard().get(f.row(), f.col());

        int dr = Math.abs(t.row() - f.row());
        int dc = Math.abs(t.col() - f.col());

        // normal king move
        if (dr <= 1 && dc <= 1) return true;

        // castling: king moves 2 squares horizontally on same rank
        if (f.row() != t.row()) return false;
        if (dr != 0 || dc != 2) return false;

        Color color = RulesUtil.colorOf(king);
        if (color == null) return false;

        // white: from (7,4) to (7,6) or (7,2)
        // black: from (0,4) to (0,6) or (0,2)
        if (color == Color.WHITE && f.row() == 7 && f.col() == 4) {
            if (t.col() == 6) {
                // king side
                if (!state.canCastleWK()) return false;
                return RulesUtil.isEmpty(state.getBoard().get(7,5)) && RulesUtil.isEmpty(state.getBoard().get(7,6))
                        && state.getBoard().get(7,7) == 'R';
            } else if (t.col() == 2) {
                // queen side
                if (!state.canCastleWQ()) return false;
                return RulesUtil.isEmpty(state.getBoard().get(7,3)) && RulesUtil.isEmpty(state.getBoard().get(7,2))
                        && RulesUtil.isEmpty(state.getBoard().get(7,1))
                        && state.getBoard().get(7,0) == 'R';
            }
        }

        if (color == Color.BLACK && f.row() == 0 && f.col() == 4) {
            if (t.col() == 6) {
                if (!state.canCastleBK()) return false;
                return RulesUtil.isEmpty(state.getBoard().get(0,5)) && RulesUtil.isEmpty(state.getBoard().get(0,6))
                        && state.getBoard().get(0,7) == 'r';
            } else if (t.col() == 2) {
                if (!state.canCastleBQ()) return false;
                return RulesUtil.isEmpty(state.getBoard().get(0,3)) && RulesUtil.isEmpty(state.getBoard().get(0,2))
                        && RulesUtil.isEmpty(state.getBoard().get(0,1))
                        && state.getBoard().get(0,0) == 'r';
            }
        }

        return false;
    }
}