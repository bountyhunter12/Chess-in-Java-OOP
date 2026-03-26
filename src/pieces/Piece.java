package pieces;

import app.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Piece {

    public int col, row;
    public int xPos, yPos;

    public boolean isWhite;
    public String name;
    public int value;
    public boolean hasMoved = false;

    protected static BufferedImage sheet;
    protected Image sprite;

    protected Board board;

    private final int sheetCols = 6;
    private final int sheetRows = 2;

    public Piece(Board board) {
        this.board = board;

        if (sheet == null) {
            try {
                sheet = ImageIO.read(new File("src/resources/pieces.png"));
                if (sheet == null) {
                    throw new IllegalArgumentException("Could not load src/resources/pieces.png");
                }
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPosition(int col, int row) {
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
    }

    public void setSprite(int sheetCol, int sheetRow) {
        if (sheet == null) return;

        int cellW = sheet.getWidth() / sheetCols;
        int cellH = sheet.getHeight() / sheetRows;

        BufferedImage sub = sheet.getSubimage(sheetCol * cellW, sheetRow * cellH, cellW, cellH);
        this.sprite = sub.getScaledInstance(board.tileSize, board.tileSize, Image.SCALE_SMOOTH);
    }

    public void draw(Graphics2D g2d) {
        if (sprite == null) return;
        g2d.drawImage(sprite, xPos, yPos, null);
    }

    public void draw(Graphics2D g2d, int drawX, int drawY, int size) {
        if (sprite == null) return;
        g2d.drawImage(sprite, drawX, drawY, size, size, null);
    }

    public boolean canMoveTo(int newCol, int newRow) {
        return false;
    }

    protected boolean isInsideBoard(int c, int r) {
        return c >= 0 && c < 8 && r >= 0 && r < 8;
    }

    protected boolean isSameSquare(int c, int r) {
        return col == c && row == r;
    }

    protected boolean hasFriendlyPiece(int c, int r) {
        Piece p = board.getPiece(c, r);
        return p != null && p.isWhite == this.isWhite;
    }

    protected boolean hasEnemyPiece(int c, int r) {
        Piece p = board.getPiece(c, r);
        return p != null && p.isWhite != this.isWhite;
    }

    protected boolean isEmpty(int c, int r) {
        return board.getPiece(c, r) == null;
    }

    protected boolean isPathClear(int targetCol, int targetRow) {
        int colStep = Integer.compare(targetCol, col);
        int rowStep = Integer.compare(targetRow, row);

        int c = col + colStep;
        int r = row + rowStep;

        while (c != targetCol || r != targetRow) {
            if (board.getPiece(c, r) != null) return false;
            c += colStep;
            r += rowStep;
        }
        return true;
    }
}