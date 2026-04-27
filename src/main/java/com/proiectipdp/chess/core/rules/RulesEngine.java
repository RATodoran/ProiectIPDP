package com.proiectipdp.chess.core.rules;

import com.proiectipdp.chess.core.*;

public class RulesEngine {

    private final MoveValidator validator = new MoveValidator();
    private final MoveApplier applier = new MoveApplier();
    private final AttackMap attackMap = new AttackMap();

    public boolean tryMove(GameState state, Move move) {
        if (!validator.isLegal(state, move)) return false;

        // Notatia SAN se calculează pe poziția ÎNAINTE de aplicare
        String san = RomanianSAN.toSAN(state, move, validator);

        // aplică mutarea
        applier.applyWithPrevEp(state, move);

        // actualizează status (CHECK / CHECKMATE / STALEMATE / IN_PROGRESS)
        updateStatus(state);

        // atașează + / # după status (după mutare, e rândul adversarului)
        if (state.getStatus() == GameStatus.CHECK) san += "+";
        else if (state.getStatus() == GameStatus.CHECKMATE) san += "#";

        // log
        state.addMoveToLog(san);
        state.getHistory().record(state.makeSnapshot());

        return true;
    }

    private String toAlg(Position p) {
        char file = (char) ('a' + p.col());
        int rank = 8 - p.row();
        return "" + file + rank;
    }

    public void updateStatus(GameState state) {
        Color sideToMove = state.getTurn();
        Position king = attackMap.findKing(state, sideToMove);
        boolean inCheck = king != null && attackMap.isSquareAttacked(state, king, sideToMove.opposite());

        boolean hasLegal = hasAnyLegalMove(state);

        if (inCheck && !hasLegal) state.setStatus(GameStatus.CHECKMATE);
        else if (!inCheck && !hasLegal) state.setStatus(GameStatus.STALEMATE);
        else if (inCheck) state.setStatus(GameStatus.CHECK);
        else state.setStatus(GameStatus.IN_PROGRESS);
    }

    private boolean hasAnyLegalMove(GameState state) {
        // brute-force: try all from-to pairs; ok for proiect
        for (int r=0; r<8; r++) {
            for (int c=0; c<8; c++) {
                char p = state.getBoard().get(r,c);
                if (RulesUtil.isEmpty(p)) continue;
                if (RulesUtil.colorOf(p) != state.getTurn()) continue;

                Position from = new Position(r,c);

                for (int rr=0; rr<8; rr++) {
                    for (int cc=0; cc<8; cc++) {
                        Position to = new Position(rr,cc);

                        // promotion handling: if pawn reaches last rank, try default queen
                        if (RulesUtil.toLowerPiece(p) == 'p' && (rr == 0 || rr == 7)) {
                            Move m = new Move(from, to, 'Q');
                            if (validator.isLegal(state, m)) return true;
                        } else {
                            Move m = new Move(from, to);
                            if (validator.isLegal(state, m)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}