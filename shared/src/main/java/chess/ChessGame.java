package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = board.getPiece(startPosition);
        if (currPiece == null) {
            return null;
        }
        //starting with every move calculated by the piece at the start pos
        ArrayList<ChessMove> validMoves = new ArrayList<>(currPiece.pieceMoves(board, startPosition));
        ArrayList<ChessMove> invalidMoves = new ArrayList<>();
        //if it would leave the king in check, remove it
        for (ChessMove move : validMoves) {
            //perform move
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            board.addPiece(move.getStartPosition(), null);
            if (isInCheck(currPiece.getTeamColor())) {
                invalidMoves.add(move);
            }
            //reverse the move (reset the board)
            board.addPiece(move.getStartPosition(), board.getPiece(move.getEndPosition()));
            board.addPiece(move.getEndPosition(), null);
        }
        validMoves.removeAll(invalidMoves);
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //only the king can be in check
        //check if the king is in the capturable pieces ArrayList (total combined of all pieces capturable arraylist)
        ArrayList<ChessPiece> allCapturableWhitePieces = new ArrayList<>();
        ArrayList<ChessPiece> allCapturableBlackPieces = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(currPosition);
                if (piece != null) {
                    if (piece.getTeamColor() == TeamColor.BLACK) {
                        allCapturableWhitePieces.addAll(piece.getCapturablePieces(board, currPosition));
                    } else {//TeamColor.WHITE
                        allCapturableBlackPieces.addAll(piece.getCapturablePieces(board, currPosition));
                    }
                }
            }
        }
        if (teamColor == TeamColor.BLACK) {
            for (ChessPiece piece : allCapturableBlackPieces) {
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return true;
                }
            }
        } else { //TeamColor.WHITE
            for (ChessPiece piece : allCapturableWhitePieces) {
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }


}
