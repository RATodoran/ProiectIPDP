package com.proiectipdp.chess.ui;

import com.proiectipdp.chess.core.GameState;
import com.proiectipdp.chess.core.Move;
import com.proiectipdp.chess.core.Position;
import com.proiectipdp.chess.core.rules.RulesEngine;
import com.proiectipdp.chess.core.rules.RulesUtil;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class BoardView extends GridPane {

    private static final Color LIGHT = Color.web("#D7D4D4");
    private static final Color DARK  = Color.web("#807B76");
    private static final int TILE_SIZE = 72;

    private Runnable onStateChanged;

    private final StackPane[][] tiles = new StackPane[8][8];
    private final Map<Character, Image> pieceImages = new HashMap<>();

    private final GameState state;
    private final RulesEngine engine;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private boolean flipped = false;

    private boolean showMoves = false;
    private boolean allowIllegal = false;
    private boolean illegalMoveMade = false;

    public void setShowMoves(boolean value) { this.showMoves = value; }
    public void setAllowIllegal(boolean value) { this.allowIllegal = value; }

    public boolean hasIllegalMove() { return illegalMoveMade; }
    public boolean isFlipped() { return flipped; }

    public void flipBoard() {
        flipped = !flipped;
        renderFromState();
    }

    public void setOnStateChanged(Runnable onStateChanged) {
        this.onStateChanged = onStateChanged;
    }

    public BoardView(GameState state, RulesEngine engine) {
        this.state = state;
        this.engine = engine;

        setPadding(new Insets(10));

        setBackground(new Background(
                new BackgroundFill(Color.web("#1E1E1E"), new CornerRadii(12), Insets.EMPTY)
        ));

        loadPieceImages();
        buildBoard();
        renderFromState();
    }

    private void buildBoard() {
        getChildren().clear();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                StackPane tile = createTile(row, col);
                tiles[row][col] = tile;

                final int r = row;
                final int c = col;

                tile.setOnMouseClicked(e -> handleClick(r, c));

                add(tile, col, row);
            }
        }
    }

    private StackPane createTile(int row, int col) {
        boolean isLight = (row + col) % 2 == 0;

        StackPane tile = new StackPane();
        tile.setPrefSize(TILE_SIZE, TILE_SIZE);

        Color base = isLight ? LIGHT : DARK;

        tile.setBackground(new Background(new BackgroundFill(base, CornerRadii.EMPTY, Insets.EMPTY)));

        return tile;
    }

    private void handleClick(int row, int col) {

        int realRow = flipped ? 7 - row : row;
        int realCol = flipped ? 7 - col : col;

        char clicked = state.getBoard().get(realRow, realCol);

        if (selectedRow == -1) {

            if (clicked == '#') return;
            if (RulesUtil.colorOf(clicked) != state.getTurn()) return;

            select(realRow, realCol);
            return;
        }

        if (realRow == selectedRow && realCol == selectedCol) {
            deselect();
            return;
        }

        tryMove(selectedRow, selectedCol, realRow, realCol);
    }

    private void tryMove(int fromRow, int fromCol, int toRow, int toCol) {

        Move move = new Move(
                new Position(fromRow, fromCol),
                new Position(toRow, toCol),
                null
        );

        boolean ok;

        if (allowIllegal) {

            // verificăm dacă era legală
            GameState copy = state.copy();
            boolean legal = engine.tryMove(copy, move);

            // aplicăm mutarea forțat (folosim engine pe state real)
            engine.tryMove(state, move); // chiar dacă e ilegală, o forțăm

            ok = true;

            if (!legal) {
                illegalMoveMade = true;
            }

        } else {
            ok = engine.tryMove(state, move);
        }

        deselect();

        if (ok) {
            renderFromState();
            if (onStateChanged != null) onStateChanged.run();
        }
    }

    private void select(int row, int col) {
        deselect();

        selectedRow = row;
        selectedCol = col;

        int displayRow = flipped ? 7 - row : row;
        int displayCol = flipped ? 7 - col : col;

        tiles[displayRow][displayCol].setBorder(new Border(
                new BorderStroke(Color.web("#F2C14E"),
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(3))
        ));

        if (showMoves) {
            highlightMoves(row, col);
        }
    }

    private void deselect() {
        renderFromState();
        selectedRow = -1;
        selectedCol = -1;
    }

    // 🔥 FARA getLegalMoves → brute force
    private void highlightMoves(int row, int col) {

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                Move move = new Move(
                        new Position(row, col),
                        new Position(r, c),
                        null
                );

                GameState copy = state.copy();

                if (engine.tryMove(copy, move)) {

                    int displayRow = flipped ? 7 - r : r;
                    int displayCol = flipped ? 7 - c : c;

                    tiles[displayRow][displayCol].setStyle(
                            "-fx-background-color: rgba(0,255,0,0.4);"
                    );
                }
            }
        }
    }

    private void renderFromState() {

        for (int displayRow = 0; displayRow < 8; displayRow++) {
            for (int displayCol = 0; displayCol < 8; displayCol++) {

                tiles[displayRow][displayCol].getChildren().clear();
                tiles[displayRow][displayCol].setStyle("");

                int boardRow = flipped ? 7 - displayRow : displayRow;
                int boardCol = flipped ? 7 - displayCol : displayCol;

                char code = state.getBoard().get(boardRow, boardCol);

                if (code != '#') {

                    ImageView iv = new ImageView(pieceImages.get(code));

                    iv.setFitWidth(TILE_SIZE * 0.85);
                    iv.setFitHeight(TILE_SIZE * 0.85);

                    tiles[displayRow][displayCol].getChildren().add(iv);
                }
            }
        }
    }

    private Image load(String path) {
        var stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            System.out.println("NU gasesc: " + path);
            return null;
        }
        return new Image(stream);
    }

    private void loadPieceImages() {

        pieceImages.put('P', load("/PieseSah/white_pawn.png"));
        pieceImages.put('R', load("/PieseSah/white_rook.png"));
        pieceImages.put('N', load("/PieseSah/white_knight.png"));
        pieceImages.put('B', load("/PieseSah/white_bishop.png"));
        pieceImages.put('Q', load("/PieseSah/white_queen.png"));
        pieceImages.put('K', load("/PieseSah/white_king.png"));

        pieceImages.put('p', load("/PieseSah/black_pawn.png"));
        pieceImages.put('r', load("/PieseSah/black_rook.png"));
        pieceImages.put('n', load("/PieseSah/black_knight.png"));
        pieceImages.put('b', load("/PieseSah/black_bishop.png"));
        pieceImages.put('q', load("/PieseSah/black_queen.png"));
        pieceImages.put('k', load("/PieseSah/black_king.png"));
    }
    public void forceRender() { renderFromState(); }
    public void deselectForExternal() { deselect(); }
}