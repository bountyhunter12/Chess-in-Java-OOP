package app;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import pieces.*;

public class Board extends JPanel {

    public int tileSize = 85;

    private final int cols = 8;
    private final int rows = 8;

    private final int topPanelHeight = 80;
    private final int bottomPanelHeight = 90;
    private final int sidePadding = 20;

    private int boardOffsetX = 0;
    private int boardOffsetY = topPanelHeight;
    private int boardPixelSize = 8 * tileSize;

    private final List<Piece> pieces = new ArrayList<>();
    private final List<Piece> capturedWhite = new ArrayList<>();
    private final List<Piece> capturedBlack = new ArrayList<>();

    private Piece selectedPiece = null;
    private final List<Point> validMoves = new ArrayList<>();

    private boolean whiteTurn = true;
    private String gameStatus = "";

    private Piece enPassantPawn = null;

    public Board() {
        setPreferredSize(new Dimension(900, 980));
        setBackground(Color.WHITE);

        setupPieces();

        Input input = new Input(this);
        addMouseListener(input);
        addMouseMotionListener(input);
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public Piece getPiece(int col, int row) {
        for (Piece p : pieces) {
            if (p.col == col && p.row == row) return p;
        }
        return null;
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public Piece getEnPassantPawn() {
        return enPassantPawn;
    }

    public void setEnPassantPawn(Piece p) {
        enPassantPawn = p;
    }

    public void addCapturedPiece(Piece p) {
        if (p.isWhite) capturedWhite.add(p);
        else capturedBlack.add(p);
    }

    public int getBoardOffsetX() {
        return boardOffsetX;
    }

    public int getBoardOffsetY() {
        return boardOffsetY;
    }

    public int getBoardPixelSize() {
        return boardPixelSize;
    }

    public void updateLayoutMetrics() {
        int availableWidth = getWidth() - sidePadding * 2;
        int availableHeight = getHeight() - topPanelHeight - bottomPanelHeight - 20;

        boardPixelSize = Math.min(availableWidth, availableHeight);
        boardPixelSize = Math.max(boardPixelSize, 160);

        tileSize = boardPixelSize / 8;
        boardPixelSize = tileSize * 8;

        boardOffsetX = (getWidth() - boardPixelSize) / 2;
        boardOffsetY = topPanelHeight;
    }

    public boolean isInsideBoardPixel(int x, int y) {
        return x >= boardOffsetX && x < boardOffsetX + boardPixelSize
                && y >= boardOffsetY && y < boardOffsetY + boardPixelSize;
    }

    public int pixelToCol(int x) {
        return (x - boardOffsetX) / tileSize;
    }

    public int pixelToRow(int y) {
        return (y - boardOffsetY) / tileSize;
    }

    public void selectPiece(Piece p) {
        if (p == null) return;
        if (p.isWhite != whiteTurn) return;

        selectedPiece = p;
        computeValidMoves();
        repaint();
    }

    public void clearSelection() {
        selectedPiece = null;
        validMoves.clear();
        repaint();
    }

    public void tryMoveSelectedTo(int col, int row) {
        if (selectedPiece == null) return;

        boolean allowed = false;
        for (Point pt : validMoves) {
            if (pt.x == col && pt.y == row) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            clearSelection();
            return;
        }

        Move move = new Move(this, selectedPiece, col, row);
        move.makeMove(this);

        whiteTurn = !whiteTurn;
        clearSelection();

        if (isKingInCheck(whiteTurn)) {
            if (isCheckmate(whiteTurn)) {
                gameStatus = (whiteTurn ? "White" : "Black") + " is checkmated!";
            } else {
                gameStatus = (whiteTurn ? "White" : "Black") + " is in check!";
            }
        } else if (isStalemate(whiteTurn)) {
            gameStatus = "Stalemate!";
        } else {
            gameStatus = "";
        }

        repaint();
    }

    private void computeValidMoves() {
        validMoves.clear();
        if (selectedPiece == null) return;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (selectedPiece.canMoveTo(c, r) && !wouldLeaveKingInCheck(selectedPiece, c, r)) {
                    validMoves.add(new Point(c, r));
                }
            }
        }
    }

    private boolean wouldLeaveKingInCheck(Piece piece, int newCol, int newRow) {
        int oldCol = piece.col;
        int oldRow = piece.row;

        Piece captured = getPiece(newCol, newRow);

        Piece epCaptured = null;
        if (piece instanceof Pawn && captured == null && newCol != oldCol) {
            Piece sidePawn = getPiece(newCol, oldRow);
            if (sidePawn instanceof Pawn && sidePawn == enPassantPawn && sidePawn.isWhite != piece.isWhite) {
                epCaptured = sidePawn;
            }
        }

        if (captured != null) pieces.remove(captured);
        if (epCaptured != null) pieces.remove(epCaptured);

        piece.setPosition(newCol, newRow);

        Piece rookMoved = null;
        int rookOldCol = -1;
        if (piece instanceof King && Math.abs(newCol - oldCol) == 2) {
            if (newCol > oldCol) {
                rookMoved = getPiece(7, oldRow);
                if (rookMoved != null) {
                    rookOldCol = rookMoved.col;
                    rookMoved.setPosition(5, oldRow);
                }
            } else {
                rookMoved = getPiece(0, oldRow);
                if (rookMoved != null) {
                    rookOldCol = rookMoved.col;
                    rookMoved.setPosition(3, oldRow);
                }
            }
        }

        boolean inCheck = isKingInCheck(piece.isWhite);

        piece.setPosition(oldCol, oldRow);

        if (rookMoved != null) {
            rookMoved.setPosition(rookOldCol, oldRow);
        }

        if (captured != null) pieces.add(captured);
        if (epCaptured != null) pieces.add(epCaptured);

        return inCheck;
    }

    public boolean isKingInCheck(boolean whiteKing) {
        Piece king = null;

        for (Piece p : pieces) {
            if (p instanceof King && p.isWhite == whiteKing) {
                king = p;
                break;
            }
        }

        if (king == null) return false;

        for (Piece p : pieces) {
            if (p.isWhite != whiteKing && p.canMoveTo(king.col, king.row)) {
                return true;
            }
        }

        return false;
    }

    public boolean isCheckmate(boolean whiteSide) {
        if (!isKingInCheck(whiteSide)) return false;

        for (Piece p : pieces) {
            if (p.isWhite == whiteSide) {
                for (int r = 0; r < 8; r++) {
                    for (int c = 0; c < 8; c++) {
                        if (p.canMoveTo(c, r) && !wouldLeaveKingInCheck(p, c, r)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean isStalemate(boolean whiteSide) {
        if (isKingInCheck(whiteSide)) return false;

        for (Piece p : pieces) {
            if (p.isWhite == whiteSide) {
                for (int r = 0; r < 8; r++) {
                    for (int c = 0; c < 8; c++) {
                        if (p.canMoveTo(c, r) && !wouldLeaveKingInCheck(p, c, r)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean canCastle(King king, int newCol, int newRow) {
        if (king.hasMoved) return false;
        if (isKingInCheck(king.isWhite)) return false;
        if (newRow != king.row) return false;

        if (newCol == king.col + 2) {
            Piece rook = getPiece(7, king.row);
            if (!(rook instanceof Rook) || rook.isWhite != king.isWhite || rook.hasMoved) return false;
            if (getPiece(5, king.row) != null || getPiece(6, king.row) != null) return false;
            if (wouldLeaveKingInCheck(king, 5, king.row)) return false;
            if (wouldLeaveKingInCheck(king, 6, king.row)) return false;
            return true;
        }

        if (newCol == king.col - 2) {
            Piece rook = getPiece(0, king.row);
            if (!(rook instanceof Rook) || rook.isWhite != king.isWhite || rook.hasMoved) return false;
            if (getPiece(1, king.row) != null || getPiece(2, king.row) != null || getPiece(3, king.row) != null) return false;
            if (wouldLeaveKingInCheck(king, 3, king.row)) return false;
            if (wouldLeaveKingInCheck(king, 2, king.row)) return false;
            return true;
        }

        return false;
    }

    public void promotePawn(Pawn pawn) {
        pieces.remove(pawn);
        Piece queen = new Queen(this, pawn.col, pawn.row, pawn.isWhite);
        queen.hasMoved = true;
        pieces.add(queen);
    }

    private int getScore(boolean whiteCapturedPieces) {
        int total = 0;
        List<Piece> list = whiteCapturedPieces ? capturedWhite : capturedBlack;
        for (Piece p : list) total += p.value;
        return total;
    }

    private void setupPieces() {
        pieces.add(new Rook(this, 0, 0, false));
        pieces.add(new Knight(this, 1, 0, false));
        pieces.add(new Bishop(this, 2, 0, false));
        pieces.add(new Queen(this, 3, 0, false));
        pieces.add(new King(this, 4, 0, false));
        pieces.add(new Bishop(this, 5, 0, false));
        pieces.add(new Knight(this, 6, 0, false));
        pieces.add(new Rook(this, 7, 0, false));
        for (int c = 0; c < 8; c++) pieces.add(new Pawn(this, c, 1, false));

        pieces.add(new Rook(this, 0, 7, true));
        pieces.add(new Knight(this, 1, 7, true));
        pieces.add(new Bishop(this, 2, 7, true));
        pieces.add(new Queen(this, 3, 7, true));
        pieces.add(new King(this, 4, 7, true));
        pieces.add(new Bishop(this, 5, 7, true));
        pieces.add(new Knight(this, 6, 7, true));
        pieces.add(new Rook(this, 7, 7, true));
        for (int c = 0; c < 8; c++) pieces.add(new Pawn(this, c, 6, true));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateLayoutMetrics();

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(new Color(245, 245, 245));
        g2d.fillRoundRect(boardOffsetX, 10, boardPixelSize, 56, 20, 20);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.drawString(whiteTurn ? "White to move" : "Black to move", boardOffsetX + 18, 35);

        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.drawString("Black captured: " + getScore(true), boardOffsetX + 18, 58);

        // board squares
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if ((c + r) % 2 == 0) g2d.setColor(new Color(227, 198, 181));
                else g2d.setColor(new Color(157, 105, 53));

                g2d.fillRect(boardOffsetX + c * tileSize, boardOffsetY + r * tileSize, tileSize, tileSize);
            }
        }

        // highlights
        g2d.setColor(new Color(80, 200, 120, 110));
        for (Point pt : validMoves) {
            g2d.fillRect(boardOffsetX + pt.x * tileSize, boardOffsetY + pt.y * tileSize, tileSize, tileSize);
        }

        // coordinates
        g2d.setColor(new Color(40, 40, 40));
        g2d.setFont(new Font("Arial", Font.BOLD, Math.max(14, tileSize / 5)));

        // files: a-h
        for (int c = 0; c < 8; c++) {
            String file = String.valueOf((char) ('a' + c));
            int x = boardOffsetX + c * tileSize + 6;
            int y = boardOffsetY + boardPixelSize - 6;
            g2d.drawString(file, x, y);
        }

        // ranks: 8-1
        for (int r = 0; r < 8; r++) {
            String rank = String.valueOf(8 - r);
            int x = boardOffsetX + 6;
            int y = boardOffsetY + r * tileSize + 18;
            g2d.drawString(rank, x, y);
        }

        // pieces
        for (Piece p : pieces) {
            int normalX = boardOffsetX + p.col * tileSize;
            int normalY = boardOffsetY + p.row * tileSize;

            boolean dragged =
                    selectedPiece == p &&
                    (p.xPos != p.col * tileSize || p.yPos != p.row * tileSize);

            if (dragged) {
                p.draw(g2d, p.xPos, p.yPos, tileSize);
            } else {
                p.draw(g2d, normalX, normalY, tileSize);
            }
        }

        int bottomY = boardOffsetY + boardPixelSize + 12;
        g2d.setColor(new Color(245, 245, 245));
        g2d.fillRoundRect(boardOffsetX, bottomY, boardPixelSize, 62, 20, 20);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.drawString("White captured: " + getScore(false), boardOffsetX + 18, bottomY + 24);

        if (!gameStatus.isEmpty()) {
            g2d.setColor(new Color(180, 30, 30));
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString(gameStatus, boardOffsetX + 18, bottomY + 48);
        }
    }
}