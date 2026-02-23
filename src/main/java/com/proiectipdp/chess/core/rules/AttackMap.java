package com.proiectipdp.chess.core.rules;

import com.proiectipdp.chess.core.Color;
import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Position;

public class AttackMap {

    public boolean isSquareAttacked(GameState state, Position sq, Color byColor) {
        int r = sq.row();
        int c = sq.col();

        // 1) pawns
        int pawnDir = (byColor == Color.WHITE) ? -1 : 1;
        int pr = r - pawnDir; // square from which pawn attacks into (r,c)
        if (RulesUtil.inBounds(pr, c - 1)) {
            char p = state.getBoard().get(pr, c - 1);
            if (p == (byColor == Color.WHITE ? 'P' : 'p')) return true;
        }
        if (RulesUtil.inBounds(pr, c + 1)) {
            char p = state.getBoard().get(pr, c + 1);
            if (p == (byColor == Color.WHITE ? 'P' : 'p')) return true;
        }

        // 2) knights
        int[][] kD = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
        char kn = (byColor == Color.WHITE) ? 'N' : 'n';
        for (int[] d : kD) {
            int rr = r + d[0], cc = c + d[1];
            if (RulesUtil.inBounds(rr, cc) && state.getBoard().get(rr, cc) == kn) return true;
        }

        // 3) king (adjacent)
        char kk = (byColor == Color.WHITE) ? 'K' : 'k';
        for (int dr=-1; dr<=1; dr++) for (int dc=-1; dc<=1; dc++) {
            if (dr==0 && dc==0) continue;
            int rr=r+dr, cc=c+dc;
            if (RulesUtil.inBounds(rr,cc) && state.getBoard().get(rr,cc)==kk) return true;
        }

        // 4) sliding: bishops/queens diagonals
        if (rayAttacked(state, r, c, 1, 1, byColor, 'b', 'q')) return true;
        if (rayAttacked(state, r, c, 1,-1, byColor, 'b', 'q')) return true;
        if (rayAttacked(state, r, c,-1, 1, byColor, 'b', 'q')) return true;
        if (rayAttacked(state, r, c,-1,-1, byColor, 'b', 'q')) return true;

        // 5) sliding: rooks/queens straight
        if (rayAttacked(state, r, c, 1, 0, byColor, 'r', 'q')) return true;
        if (rayAttacked(state, r, c,-1, 0, byColor, 'r', 'q')) return true;
        if (rayAttacked(state, r, c, 0, 1, byColor, 'r', 'q')) return true;
        if (rayAttacked(state, r, c, 0,-1, byColor, 'r', 'q')) return true;

        return false;
    }

    private boolean rayAttacked(GameState state, int r, int c, int dr, int dc, Color byColor, char piece1, char piece2) {
        int rr = r + dr;
        int cc = c + dc;

        while (RulesUtil.inBounds(rr, cc)) {
            char x = state.getBoard().get(rr, cc);
            if (!RulesUtil.isEmpty(x)) {
                if (RulesUtil.colorOf(x) == byColor) {
                    char low = RulesUtil.toLowerPiece(x);
                    if (low == piece1 || low == piece2) return true;
                }
                return false; // blocked by any piece
            }
            rr += dr;
            cc += dc;
        }
        return false;
    }

    public Position findKing(GameState state, Color color) {
        char k = (color == Color.WHITE) ? 'K' : 'k';
        for (int r=0; r<8; r++) {
            for (int c=0; c<8; c++) {
                if (state.getBoard().get(r,c) == k) return new Position(r,c);
            }
        }
        return null;
    }
}