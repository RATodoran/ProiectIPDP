package com.proiectipdp.chess.core;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.proiectipdp.chess.core.replay.History;
import com.proiectipdp.chess.core.replay.Snapshot;
public class GameState {

    private Board board = new Board();
    private Color turn = Color.WHITE;
    private final List<String> moveLog = new ArrayList<>();
    // castling rights
    private boolean wk = true, wq = true, bk = true, bq = true;

    // en passant target (pătrat pe care poate captura pionul)
    private Position enPassantTarget = null;

    private GameStatus status = GameStatus.IN_PROGRESS;


    public List<String> getMoveLog() {
        return Collections.unmodifiableList(moveLog);
    }

    public void addMoveToLog(String s) {
        moveLog.add(s);
    }

    public GameState() {
        initStandard();
    }

    public Board getBoard() {
        return board;
    }

    public Color getTurn() {
        return turn;
    }

    public void setTurn(Color turn) {
        this.turn = turn;
    }

    public boolean canCastleWK() { return wk; }
    public boolean canCastleWQ() { return wq; }
    public boolean canCastleBK() { return bk; }
    public boolean canCastleBQ() { return bq; }

    public void setCastleWK(boolean v) { wk = v; }
    public void setCastleWQ(boolean v) { wq = v; }
    public void setCastleBK(boolean v) { bk = v; }
    public void setCastleBQ(boolean v) { bq = v; }

    private final History history = new History();

    public History getHistory() {
        return history;
    }

    public Position getEnPassantTarget() { return enPassantTarget; }
    public void setEnPassantTarget(Position p) { enPassantTarget = p; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus s) { status = s; }

    public void initStandard() {
        board.clear();

        // negre
        char[] blackBack = {'r','n','b','q','k','b','n','r'};
        for (int c=0; c<8; c++) board.set(0,c, blackBack[c]);
        for (int c=0; c<8; c++) board.set(1,c, 'p');

        // albe
        for (int c=0; c<8; c++) board.set(6,c, 'P');
        char[] whiteBack = {'R','N','B','Q','K','B','N','R'};
        for (int c=0; c<8; c++) board.set(7,c, whiteBack[c]);

        turn = Color.WHITE;
        wk = wq = bk = bq = true;
        enPassantTarget = null;
        status = GameStatus.IN_PROGRESS;

        moveLog.clear();

        history.clear();
        history.record(makeSnapshot());
    }

    public GameState copy() {
        GameState s = new GameState();
        s.board = this.board.copy();
        s.turn = this.turn;
        s.wk = this.wk; s.wq = this.wq; s.bk = this.bk; s.bq = this.bq;
        s.enPassantTarget = this.enPassantTarget == null ? null : new Position(this.enPassantTarget.row(), this.enPassantTarget.col());
        s.status = this.status;
        return s;
    }

    public Snapshot makeSnapshot() {
        return new Snapshot(
                board.copySquares(),
                turn,
                wk, wq, bk, bq,
                enPassantTarget == null ? null :
                        new Position(enPassantTarget.row(), enPassantTarget.col()),
                status,
                moveLog
        );
    }

    public void loadSnapshot(Snapshot s) {

        board.clear();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board.set(r, c, s.board[r][c]);
            }
        }

        turn = s.turn;

        wk = s.wk;
        wq = s.wq;
        bk = s.bk;
        bq = s.bq;

        enPassantTarget =
                s.enPassantTarget == null
                        ? null
                        : new Position(
                        s.enPassantTarget.row(),
                        s.enPassantTarget.col()
                );

        status = s.status;

        moveLog.clear();
        moveLog.addAll(s.moveLog);
    }
}