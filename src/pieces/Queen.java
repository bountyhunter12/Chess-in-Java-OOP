package pieces;

import app.Board;

public class Queen extends Piece {

    public Queen(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.isWhite = isWhite;
        this.name = "Queen";
        this.value = 9;
        setPosition(col, row);

        int sheetCol = 1;
        int sheetRow = isWhite ? 0 : 1;
        setSprite(sheetCol, sheetRow);
    }

    @Override
    public boolean canMoveTo(int newCol, int newRow) {
        if (!isInsideBoard(newCol, newRow) || isSameSquare(newCol, newRow) || hasFriendlyPiece(newCol, newRow)) return false;

        boolean rookLike = (newCol == col || newRow == row);
        boolean bishopLike = (Math.abs(newCol - col) == Math.abs(newRow - row));

        if (!rookLike && !bishopLike) return false;
        return isPathClear(newCol, newRow);
    }
}