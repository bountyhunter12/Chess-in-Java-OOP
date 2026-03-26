package pieces;

import app.Board;

public class Knight extends Piece {

    public Knight(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.isWhite = isWhite;
        this.name = "Knight";
        this.value = 3;
        setPosition(col, row);

        int sheetCol = 3;
        int sheetRow = isWhite ? 0 : 1;
        setSprite(sheetCol, sheetRow);
    }

    @Override
    public boolean canMoveTo(int newCol, int newRow) {
        if (!isInsideBoard(newCol, newRow) || isSameSquare(newCol, newRow) || hasFriendlyPiece(newCol, newRow)) return false;

        int dc = Math.abs(newCol - col);
        int dr = Math.abs(newRow - row);

        return (dc == 2 && dr == 1) || (dc == 1 && dr == 2);
    }
}