package pieces;

import app.Board;

public class Rook extends Piece {

    public Rook(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.isWhite = isWhite;
        this.name = "Rook";
        this.value = 5;
        setPosition(col, row);

        int sheetCol = 4;
        int sheetRow = isWhite ? 0 : 1;
        setSprite(sheetCol, sheetRow);
    }

    @Override
    public boolean canMoveTo(int newCol, int newRow) {
        if (!isInsideBoard(newCol, newRow)) return false;
        if (isSameSquare(newCol, newRow)) return false;
        if (hasFriendlyPiece(newCol, newRow)) return false;

        if (newCol != col && newRow != row) return false;

        return isPathClear(newCol, newRow);
    }
}