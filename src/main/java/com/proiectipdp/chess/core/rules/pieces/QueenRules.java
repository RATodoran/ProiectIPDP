package com.proiectipdp.chess.core.rules.pieces;

import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Move;
import com.proiectipdp.chess.core.Position;

public class QueenRules implements PieceRules {

    private final BishopRules bishop = new BishopRules();
    private final RookRules rook = new RookRules();

    @Override
    public boolean isPseudoLegal(GameState state, Move move) {
        Position f = move.from();
        Position t = move.to();

        if (f.row() == t.row() || f.col() == t.col()) {
            return rook.isPseudoLegal(state, move);
        }
        return bishop.isPseudoLegal(state, move);
    }
}