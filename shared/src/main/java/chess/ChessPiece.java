package chess;

import javax.swing.text.TabExpander;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public enum direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UPRIGHT,
        DOWNRIGHT,
        DOWNLEFT,
        UPLEFT
    }

    public ChessMove calculateMoveCoords(ChessPosition myPosition, direction myDirection) {
        if (myDirection == direction.UP ) {
            return new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null);
        } else if (myDirection == direction.DOWN ) {
            return new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null);
        } else if (myDirection == direction.LEFT) {
            return new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1), null);
        } else if (myDirection == direction.RIGHT) {
            return new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1), null);
        } else if (myDirection == direction.UPRIGHT ) {
            return new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), null);
        } else if (myDirection == direction.DOWNRIGHT ) {
            return new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), null);
        } else if (myDirection == direction.DOWNLEFT) {
            return new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), null);
        } else if (myDirection == direction.UPLEFT) {
            return new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), null);
        } else {
            return null;
        }
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
//        moves.add(new ChessMove(new ChessPosition(1,1), new ChessPosition(2,2), null));
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeamColor = myPiece.getTeamColor();

        switch (myPiece.getPieceType()) {
            case KING:
                //myPosition = position on the board - NOT INDEXES, ROWS AND COLUMNS
                //edge of board check, then open square check

                //+ directions
                //up
                if (myPosition.getRow() != 8) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(myPosition, direction.UP).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(myPosition, direction.UP).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(myPosition, direction.UP));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(myPosition, direction.UP));
                    }
                }
                //down
                if (myPosition.getRow() != 1) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(myPosition, direction.DOWN).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(myPosition, direction.DOWN).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(myPosition, direction.DOWN));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(myPosition, direction.DOWN));
                    }
                }
                //left
                if (myPosition.getColumn() != 1) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(myPosition, direction.LEFT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(myPosition, direction.LEFT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(myPosition, direction.LEFT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(myPosition, direction.LEFT));
                    }
                }
                //right
                if (myPosition.getColumn() != 8) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(myPosition, direction.RIGHT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(myPosition, direction.RIGHT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(myPosition, direction.RIGHT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(myPosition, direction.RIGHT));
                    }
                }

                //x directions
                //upright
                if ((myPosition.getRow() != 8) && (myPosition.getColumn() != 8)) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(myPosition, direction.UPRIGHT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(myPosition, direction.UPRIGHT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(myPosition, direction.UPRIGHT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(myPosition, direction.UPRIGHT));
                    }
                }
                //downright
                if ((myPosition.getRow() != 1) && (myPosition.getColumn() != 8)) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(myPosition, direction.DOWNRIGHT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(myPosition, direction.DOWNRIGHT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(myPosition, direction.DOWNRIGHT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(myPosition, direction.DOWNRIGHT));
                    }
                }
                //downleft
                if ((myPosition.getRow() != 1) && (myPosition.getColumn() != 1)) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(myPosition, direction.DOWNLEFT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(myPosition, direction.DOWNLEFT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(myPosition, direction.DOWNLEFT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(myPosition, direction.DOWNLEFT));
                    }
                }
                //upleft
                if ((myPosition.getRow() != 8) && (myPosition.getColumn() != 1)) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(myPosition, direction.UPLEFT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(myPosition, direction.UPLEFT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(myPosition, direction.UPLEFT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(myPosition, direction.UPLEFT));
                    }
                }
                break;
            case QUEEN:

                break;
            case BISHOP:
                break;
            case KNIGHT:
                break;
            case ROOK:
                break;
            case PAWN:
                break;
            default:
                System.out.println("Unknown piece type: " + type);
        }
        System.out.println(moves.toString());
        return moves;
//        throw new RuntimeException("Not implemented");
    }

//    Collection<ChessMove>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        if (color != that.color) return false;
        if (type != that.type) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = color.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
//        return String.format("%s %s}", color, type);
        String letter = "a";
        if (type == ChessPiece.PieceType.KING) {
            letter = "k";
        } else if (type == ChessPiece.PieceType.QUEEN) {
            letter = "q";
        } else if (type == ChessPiece.PieceType.BISHOP) {
            letter = "b";
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            letter = "n";
        } else if (type == ChessPiece.PieceType.ROOK) {
            letter = "r";
        } else if (type == ChessPiece.PieceType.PAWN) {
            letter = "p";
        } else {
            letter = "z";
        }
        if (color == ChessGame.TeamColor.BLACK) {
            return letter.toUpperCase();
        } else {
            return letter.toLowerCase();
        }
    }

}
