package com.proiectipdp.chess.core.rules.pieces;

import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Move;
import com.proiectipdp.chess.core.Position;
import com.proiectipdp.chess.core.rules.RulesUtil;

public class BishopRules implements PieceRules {

    @Override
    public boolean isPseudoLegal(GameState state, Move move) {
        Position f = move.from();
        Position t = move.to();

        int dr = t.row() - f.row();
        int dc = t.col() - f.col();
        if (Math.abs(dr) != Math.abs(dc)) return false;

        int stepR = Integer.compare(dr, 0);
        int stepC = Integer.compare(dc, 0);

        int r = f.row() + stepR;
        int c = f.col() + stepC;
        while (r != t.row() && c != t.col()) {
            if (!RulesUtil.isEmpty(state.getBoard().get(r, c))) return false;
            r += stepR;
            c += stepC;
        }
        return true;
    }
}