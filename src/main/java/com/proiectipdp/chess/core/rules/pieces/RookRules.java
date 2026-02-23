package com.proiectipdp.chess.core.rules.pieces;

import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Move;
import com.proiectipdp.chess.core.Position;
import com.proiectipdp.chess.core.rules.RulesUtil;

public class RookRules implements PieceRules {

    @Override
    public boolean isPseudoLegal(GameState state, Move move) {
        Position f = move.from();
        Position t = move.to();

        if (f.row() != t.row() && f.col() != t.col()) return false;

        int stepR = Integer.compare(t.row() - f.row(), 0);
        int stepC = Integer.compare(t.col() - f.col(), 0);

        int r = f.row() + stepR;
        int c = f.col() + stepC;
        while (r != t.row() || c != t.col()) {
            if (!RulesUtil.isEmpty(state.getBoard().get(r, c))) return false;
            r += stepR;
            c += stepC;
        }
        return true;
    }
}