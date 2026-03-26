# Chess Game in Java

A desktop Chess game built with **Java**.  
This project includes a playable chess board, piece movement, turn-based play, legal move highlighting, check/checkmate detection, stalemate, castling, en passant, pawn promotion, and a responsive fullscreen-friendly UI.

## Features

- Java Swing GUI
- Drag-and-drop piece movement
- Click-to-move support
- Legal move highlighting
- Turn-based gameplay
- Check detection
- Checkmate detection
- Stalemate detection
- Castling
- En passant
- Automatic pawn promotion
- Captured piece scoring
- Responsive board layout
- Fullscreen-friendly interface
- Board coordinates (`a-h`, `1-8`)
## UI

Here is a preview of the Chess game interface:

<img width="1907" height="1020" alt="image" src="https://github.com/user-attachments/assets/a6ed96d2-7834-46f4-bd99-1335251125c4" />

## Project Structure

```text
src/
  app/
    App.java
    Board.java
    Input.java
    Move.java
  pieces/
    Piece.java
    Pawn.java
    Rook.java
    Knight.java
    Bishop.java
    Queen.java
    King.java
  resources/
    pieces.png
.github/
  workflows/
    java-ci.yml
```
## How to Run Locally

### Compile

```bash
javac -d out src/app/*.java src/pieces/*.java
```

# RUN

```bash
java -cp out app.App
```

# Controls

- Click a piece to select it
- Click a highlighted square to move it
- Drag and drop a piece to move it
- Click outside the board to clear selection

# Chess Rules Implemented

This project supports:

- Standard piece movement
- Captures
- Check
- Checkmate
- Stalemate
- Castling
- En passant
- Pawn promotion
