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

    // Culori extrase din poza ta
    private static final Color LIGHT = Color.web("#D7D4D4");
    private static final Color DARK  = Color.web("#807B76");
    private static final int TILE_SIZE = 72;
    private Runnable onStateChanged;
    private final StackPane[][] tiles = new StackPane[8][8];
    private final Map<Character, Image> pieceImages = new HashMap<>();

    private final GameState state;
    private final RulesEngine engine;

    // selecție curentă
    private int selectedRow = -1;
    private int selectedCol = -1;

    public void setOnStateChanged(Runnable onStateChanged) {
        this.onStateChanged = onStateChanged;
    }
    public BoardView(GameState state, RulesEngine engine) {
        this.state = state;
        this.engine = engine;

        setHgap(0);
        setVgap(0);
        setPadding(new Insets(10));

        // fundal discret în jurul tablei
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

        // hover efect (dar păstrează selecția)
        tile.setOnMouseEntered(e -> {
            if (!(row == selectedRow && col == selectedCol)) {
                tile.setBorder(new Border(
                        new BorderStroke(Color.web("#F2C14E"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))
                ));
            }
        });

        tile.setOnMouseExited(e -> {
            if (!(row == selectedRow && col == selectedCol)) {
                tile.setBorder(null);
            }
        });

        return tile;
    }

    // ===== click logic =====
    private void handleClick(int row, int col) {
        char clicked = state.getBoard().get(row, col);

        // dacă nu e nimic selectat
        if (selectedRow == -1) {
            if (clicked == '#') return;

            // selectează doar piesa jucătorului curent
            if (RulesUtil.colorOf(clicked) != state.getTurn()) return;

            select(row, col);
            return;
        }

        // click pe aceeași piesă => deselect
        if (row == selectedRow && col == selectedCol) {
            deselect();
            return;
        }

        // dacă dai click pe o piesă de aceeași culoare => schimbă selecția
        if (clicked != '#') {
            if (RulesUtil.colorOf(clicked) == state.getTurn()) {
                select(row, col);
            } else {
                // piesă adversă => încercare captură
                tryMove(selectedRow, selectedCol, row, col);
            }
            return;
        }

        // pătrat gol => încercare mutare
        tryMove(selectedRow, selectedCol, row, col);
    }

    private void tryMove(int fromRow, int fromCol, int toRow, int toCol) {
        Move move = new Move(new Position(fromRow, fromCol), new Position(toRow, toCol));

        boolean ok = engine.tryMove(state, move);

        deselect();

        if (ok) {
            renderFromState();
            if (onStateChanged != null) onStateChanged.run();
        } else {
            // opțional: feedback vizual/sonor; momentan doar ignorăm
        }
    }

    private void select(int row, int col) {
        deselect();

        selectedRow = row;
        selectedCol = col;

        tiles[row][col].setBorder(new Border(
                new BorderStroke(Color.web("#F2C14E"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))
        ));
    }

    private void deselect() {
        if (selectedRow != -1) {
            tiles[selectedRow][selectedCol].setBorder(null);
        }
        selectedRow = -1;
        selectedCol = -1;
    }

    // ===== render =====
    private void renderFromState() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                tiles[r][c].getChildren().clear();
                char code = state.getBoard().get(r, c);

                if (code != '#') {
                    Image img = pieceImages.get(code);
                    if (img == null) {
                        throw new RuntimeException("Nu am imagine pentru piesa: " + code);
                    }

                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(TILE_SIZE * 0.85);
                    iv.setFitHeight(TILE_SIZE * 0.85);
                    iv.setPreserveRatio(true);
                    iv.setSmooth(true);

                    tiles[r][c].getChildren().add(iv);
                }
            }
        }
    }

    // ===== images =====
    private void loadPieceImages() {
        pieceImages.put('P', loadImage("/PieseSah/white_pawn.png"));
        pieceImages.put('R', loadImage("/PieseSah/white_rook.png"));
        pieceImages.put('N', loadImage("/PieseSah/white_knight.png"));
        pieceImages.put('B', loadImage("/PieseSah/white_bishop.png"));
        pieceImages.put('Q', loadImage("/PieseSah/white_queen.png"));
        pieceImages.put('K', loadImage("/PieseSah/white_king.png"));

        pieceImages.put('p', loadImage("/PieseSah/black_pawn.png"));
        pieceImages.put('r', loadImage("/PieseSah/black_rook.png"));
        pieceImages.put('n', loadImage("/PieseSah/black_knight.png"));
        pieceImages.put('b', loadImage("/PieseSah/black_bishop.png"));
        pieceImages.put('q', loadImage("/PieseSah/black_queen.png"));
        pieceImages.put('k', loadImage("/PieseSah/black_king.png"));
    }

    private Image loadImage(String path) {
        var stream = getClass().getResourceAsStream(path);
        if (stream == null) throw new RuntimeException("Nu găsesc imaginea: " + path);
        return new Image(stream);
    }
}