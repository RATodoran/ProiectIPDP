package com.proiectipdp.chess.core.rules;

import com.proiectipdp.chess.core.rules.pieces.*;

public class PieceMoveResolver {

    private final PawnRules pawnRules = new PawnRules();
    private final KnightRules knightRules = new KnightRules();
    private final BishopRules bishopRules = new BishopRules();
    private final RookRules rookRules = new RookRules();
    private final QueenRules queenRules = new QueenRules();
    private final KingRules kingRules = new KingRules();

    public PieceRules rulesFor(char piece) {
        char p = RulesUtil.toLowerPiece(piece);
        return switch (p) {
            case 'p' -> pawnRules;
            case 'n' -> knightRules;
            case 'b' -> bishopRules;
            case 'r' -> rookRules;
            case 'q' -> queenRules;
            case 'k' -> kingRules;
            default -> null;
        };
    }
}