package app;

import pieces.*;

public class Move {

    int newCol, newRow;
    Piece piece;
    Piece capture;

    public Move(Board board, Piece piece, int newCol, int newRow) {
        this.newCol = newCol;
        this.newRow = newRow;
        this.piece = piece;
        this.capture = board.getPiece(newCol, newRow);

        // en passant capture
        if (piece instanceof Pawn && capture == null && newCol != piece.col) {
            Piece sidePawn = board.getPiece(newCol, piece.row);
            if (sidePawn instanceof Pawn && sidePawn == board.getEnPassantPawn()) {
                this.capture = sidePawn;
            }
        }
    }

    public void makeMove(Board board) {
        int oldCol = piece.col;
        int oldRow = piece.row;

        if (capture != null) {
            board.getPieces().remove(capture);
            board.addCapturedPiece(capture);
        }

        piece.setPosition(newCol, newRow);
        piece.hasMoved = true;

        // castling rook move
        if (piece instanceof King && Math.abs(newCol - oldCol) == 2) {
            if (newCol > oldCol) {
                // king side
                Piece rook = board.getPiece(7, oldRow);
                if (rook instanceof Rook) {
                    rook.setPosition(5, oldRow);
                    rook.hasMoved = true;
                }
            } else {
                // queen side
                Piece rook = board.getPiece(0, oldRow);
                if (rook instanceof Rook) {
                    rook.setPosition(3, oldRow);
                    rook.hasMoved = true;
                }
            }
        }

        // set/reset en passant target
        if (piece instanceof Pawn && Math.abs(newRow - oldRow) == 2) {
            board.setEnPassantPawn(piece);
        } else {
            board.setEnPassantPawn(null);
        }

        // promotion
        if (piece instanceof Pawn) {
            if ((piece.isWhite && newRow == 0) || (!piece.isWhite && newRow == 7)) {
                board.promotePawn((Pawn) piece);
            }
        }

        board.repaint();
    }
}