package com.proiectipdp.chess.core.rules.pieces;

import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Move;
import com.proiectipdp.chess.core.Position;
import com.proiectipdp.chess.core.rules.RulesUtil;

public class KnightRules implements PieceRules {

    @Override
    public boolean isPseudoLegal(GameState state, Move move) {
        Position f = move.from();
        Position t = move.to();
        int dr = Math.abs(t.row() - f.row());
        int dc = Math.abs(t.col() - f.col());
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }
}