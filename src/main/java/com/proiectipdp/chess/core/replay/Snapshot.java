package com.proiectipdp.chess.core.replay;

import com.proiectipdp.chess.core.Color;
import com.proiectipdp.chess.core.GameStatus;
import com.proiectipdp.chess.core.Position;

import java.util.ArrayList;
import java.util.List;

public class Snapshot {
    public final char[][] board;          // deep copy
    public final Color turn;
    public final boolean wk, wq, bk, bq;
    public final Position enPassantTarget; // may be null
    public final GameStatus status;
    public final List<String> moveLog;     // copy

    public Snapshot(char[][] board, Color turn, boolean wk, boolean wq, boolean bk, boolean bq,
                    Position enPassantTarget, GameStatus status, List<String> moveLog) {
        this.board = board;
        this.turn = turn;
        this.wk = wk; this.wq = wq; this.bk = bk; this.bq = bq;
        this.enPassantTarget = enPassantTarget;
        this.status = status;
        this.moveLog = new ArrayList<>(moveLog);
    }
}