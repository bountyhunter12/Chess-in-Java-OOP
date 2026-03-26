package app;

import java.awt.event.*;
import pieces.Piece;

public class Input implements MouseListener, MouseMotionListener {

    private final Board board;
    private boolean isDragging = false;

    public Input(Board board) {
        this.board = board;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        board.updateLayoutMetrics();

        if (!board.isInsideBoardPixel(e.getX(), e.getY())) {
            board.clearSelection();
            return;
        }

        int col = board.pixelToCol(e.getX());
        int row = board.pixelToRow(e.getY());

        Piece clicked = board.getPiece(col, row);
        isDragging = false;

        if (clicked != null) {
            if (board.getSelectedPiece() == clicked) {
                board.clearSelection();
            } else {
                if (board.getSelectedPiece() != null && clicked.isWhite != board.getSelectedPiece().isWhite) {
                    board.tryMoveSelectedTo(col, row);
                } else {
                    board.selectPiece(clicked);
                }
            }
        } else {
            board.tryMoveSelectedTo(col, row);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Piece p = board.getSelectedPiece();
        if (p == null) return;

        if (!board.isInsideBoardPixel(e.getX(), e.getY())) return;

        isDragging = true;
        p.xPos = e.getX() - board.tileSize / 2;
        p.yPos = e.getY() - board.tileSize / 2;

        board.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Piece p = board.getSelectedPiece();
        if (p == null) return;

        board.updateLayoutMetrics();

        if (isDragging) {
            if (board.isInsideBoardPixel(e.getX(), e.getY())) {
                int newCol = board.pixelToCol(e.getX());
                int newRow = board.pixelToRow(e.getY());
                board.tryMoveSelectedTo(newCol, newRow);
            } else {
                board.clearSelection();
            }

            p.setPosition(p.col, p.row);
            board.repaint();
        }

        isDragging = false;
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
}