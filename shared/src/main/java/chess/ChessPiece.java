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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
//        moves.add(new ChessMove(new ChessPosition(1,1), new ChessPosition(2,2), null));
        ChessPiece piece = board.getPiece(myPosition);
        switch (piece.getPieceType()) {
            case KING:
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
