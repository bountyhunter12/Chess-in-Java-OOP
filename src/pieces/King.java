package pieces;

import app.Board;

public class King extends Piece {

    public King(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.isWhite = isWhite;
        this.name = "King";
        this.value = 1000;
        setPosition(col, row);

        int sheetCol = 0;
        int sheetRow = isWhite ? 0 : 1;
        setSprite(sheetCol, sheetRow);
    }

    @Override
    public boolean canMoveTo(int newCol, int newRow) {
        if (!isInsideBoard(newCol, newRow) || isSameSquare(newCol, newRow) || hasFriendlyPiece(newCol, newRow)) {
            return false;
        }

        int dc = Math.abs(newCol - col);
        int dr = Math.abs(newRow - row);

        // normal king move
        if (dc <= 1 && dr <= 1) {
            return true;
        }

        // castling attempt
        if (!hasMoved && dr == 0 && dc == 2) {
            return board.canCastle(this, newCol, newRow);
        }

        return false;
    }
}