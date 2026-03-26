package pieces;

import app.Board;

public class Pawn extends Piece {

    public Pawn(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.isWhite = isWhite;
        this.name = "Pawn";
        this.value = 1;
        setPosition(col, row);

        int sheetCol = 5;
        int sheetRow = isWhite ? 0 : 1;
        setSprite(sheetCol, sheetRow);
    }

    @Override
    public boolean canMoveTo(int newCol, int newRow) {
        if (!isInsideBoard(newCol, newRow) || isSameSquare(newCol, newRow)) return false;

        int dir = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;

        // forward 1
        if (newCol == col && newRow == row + dir && isEmpty(newCol, newRow)) {
            return true;
        }

        // forward 2 from start
        if (newCol == col && row == startRow && newRow == row + 2 * dir
                && isEmpty(newCol, row + dir) && isEmpty(newCol, newRow)) {
            return true;
        }

        // normal capture
        if (Math.abs(newCol - col) == 1 && newRow == row + dir && hasEnemyPiece(newCol, newRow)) {
            return true;
        }

        // en passant
        if (Math.abs(newCol - col) == 1 && newRow == row + dir && isEmpty(newCol, newRow)) {
            Piece lastDouble = board.getEnPassantPawn();
            if (lastDouble instanceof Pawn
                    && lastDouble.row == row
                    && lastDouble.col == newCol
                    && lastDouble.isWhite != this.isWhite) {
                return true;
            }
        }

        return false;
    }
}