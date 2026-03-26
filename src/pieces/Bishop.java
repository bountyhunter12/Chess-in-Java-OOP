package pieces;

import app.Board;

public class Bishop extends Piece {

    public Bishop(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.isWhite = isWhite;
        this.name = "Bishop";
        this.value = 3;
        setPosition(col, row);

        int sheetCol = 2;
        int sheetRow = isWhite ? 0 : 1;
        setSprite(sheetCol, sheetRow);
    }

    @Override
    public boolean canMoveTo(int newCol, int newRow) {
        if (!isInsideBoard(newCol, newRow) || isSameSquare(newCol, newRow) || hasFriendlyPiece(newCol, newRow)) return false;
        if (Math.abs(newCol - col) != Math.abs(newRow - row)) return false;
        return isPathClear(newCol, newRow);
    }
}