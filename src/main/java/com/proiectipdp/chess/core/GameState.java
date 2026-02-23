package com.proiectipdp.chess.core;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
}