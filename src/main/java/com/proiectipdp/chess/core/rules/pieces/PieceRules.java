package com.proiectipdp.chess.core.rules.pieces;

import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Move;

public interface PieceRules {
    boolean isPseudoLegal(GameState state, Move move);
}