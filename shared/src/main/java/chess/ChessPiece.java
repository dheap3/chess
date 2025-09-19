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

    private Boolean onBoard(ChessPosition position, ChessPiece.direction direction) {
        return switch (direction) {
            case UP -> position.getRow() != 8;
            case DOWN -> position.getRow() != 1;
            case LEFT -> position.getColumn() != 1;
            case RIGHT -> position.getColumn() != 8;
            case UPRIGHT -> (position.getRow() != 8) && (position.getColumn() != 8);
            case DOWNRIGHT -> (position.getRow() != 1) && (position.getColumn() != 8);
            case DOWNLEFT -> (position.getRow() != 1) && (position.getColumn() != 1);
            case UPLEFT -> (position.getRow() != 8) && (position.getColumn() != 1);
            default -> false;
        };
    }

    //a recursive helper function to simplify each direction and also apply recursive ability for moving multiple spaces
    ArrayList<ChessMove> getMoves(ChessBoard board, ChessPosition position, direction dir, Boolean recursive) {
        ChessPosition tempPos = position;
        ArrayList<ChessMove> tempMoves = new ArrayList<>();
        ChessPiece myTempPiece = board.getPiece(position);
        ChessGame.TeamColor myTeamColor = myTempPiece.getTeamColor();

        while (onBoard(tempPos, dir)) {
            //if there's a piece there
            if (board.getPiece(calculateMoveCoords(null, tempPos, dir).getEndPosition()) != null) {
                //check the piece color
                if (board.getPiece(calculateMoveCoords(null, tempPos, dir).getEndPosition()).getTeamColor() != myTeamColor) {
                    tempMoves.add(calculateMoveCoords(null, position, dir));
                }
            } else {
                //no piece there, so we can add it
                tempMoves.add(calculateMoveCoords(null, position, dir));
            }
            if (!recursive) {
                break;
            }
            //it's recursive
            tempPos = switch (dir) {
                case UP -> new ChessPosition(tempPos.getRow() + 1, tempPos.getColumn());
                case DOWN -> new ChessPosition(tempPos.getRow() - 1, tempPos.getColumn());
                case LEFT -> new ChessPosition(tempPos.getRow(), tempPos.getColumn() - 1);
                case RIGHT -> new ChessPosition(tempPos.getRow(), tempPos.getColumn() + 1);
                case UPRIGHT -> new ChessPosition(tempPos.getRow() + 1, tempPos.getColumn() + 1);
                case DOWNRIGHT -> new ChessPosition(tempPos.getRow() - 1, tempPos.getColumn() + 1);
                case DOWNLEFT -> new ChessPosition(tempPos.getRow() - 1, tempPos.getColumn() - 1);
                case UPLEFT -> new ChessPosition(tempPos.getRow() + 1, tempPos.getColumn() - 1);
            };

        }
        return tempMoves;
    }
    //prePosition added for recursive calls with pieces ex: queen
    public ChessMove calculateMoveCoords(ChessPosition prePosition, ChessPosition myPosition, direction myDirection) {
        if (prePosition == null){
            prePosition = myPosition;
        }
        if (myDirection == direction.UP ) {
            return new ChessMove(prePosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null);
        } else if (myDirection == direction.DOWN ) {
            return new ChessMove(prePosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null);
        } else if (myDirection == direction.LEFT) {
            return new ChessMove(prePosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1), null);
        } else if (myDirection == direction.RIGHT) {
            return new ChessMove(prePosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1), null);
        } else if (myDirection == direction.UPRIGHT ) {
            return new ChessMove(prePosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), null);
        } else if (myDirection == direction.DOWNRIGHT ) {
            return new ChessMove(prePosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), null);
        } else if (myDirection == direction.DOWNLEFT) {
            return new ChessMove(prePosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), null);
        } else if (myDirection == direction.UPLEFT) {
            return new ChessMove(prePosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), null);
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
                    if (board.getPiece(calculateMoveCoords(null, myPosition, direction.UP).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(null, myPosition, direction.UP).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(null, myPosition, direction.UP));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(null, myPosition, direction.UP));
                    }
                }
                //down
                if (myPosition.getRow() != 1) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(null, myPosition, direction.DOWN).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(null, myPosition, direction.DOWN).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(null, myPosition, direction.DOWN));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(null, myPosition, direction.DOWN));
                    }
                }
                //left
                if (myPosition.getColumn() != 1) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(null, myPosition, direction.LEFT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(null, myPosition, direction.LEFT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(null, myPosition, direction.LEFT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(null, myPosition, direction.LEFT));
                    }
                }
                //right
                if (myPosition.getColumn() != 8) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(null, myPosition, direction.RIGHT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(null, myPosition, direction.RIGHT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(null, myPosition, direction.RIGHT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(null, myPosition, direction.RIGHT));
                    }
                }

                //x directions
                //upright
                if ((myPosition.getRow() != 8) && (myPosition.getColumn() != 8)) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(null, myPosition, direction.UPRIGHT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(null, myPosition, direction.UPRIGHT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(null, myPosition, direction.UPRIGHT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(null, myPosition, direction.UPRIGHT));
                    }
                }
                //downright
                if ((myPosition.getRow() != 1) && (myPosition.getColumn() != 8)) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(null, myPosition, direction.DOWNRIGHT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(null, myPosition, direction.DOWNRIGHT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(null, myPosition, direction.DOWNRIGHT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(null, myPosition, direction.DOWNRIGHT));
                    }
                }
                //downleft
                if ((myPosition.getRow() != 1) && (myPosition.getColumn() != 1)) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(null, myPosition, direction.DOWNLEFT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(null, myPosition, direction.DOWNLEFT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(null, myPosition, direction.DOWNLEFT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(null, myPosition, direction.DOWNLEFT));
                    }
                }
                //upleft
                if ((myPosition.getRow() != 8) && (myPosition.getColumn() != 1)) {
                    //if there's a piece there
                    if (board.getPiece(calculateMoveCoords(null, myPosition, direction.UPLEFT).getEndPosition()) != null) {
                        //check the piece color
                        if (board.getPiece(calculateMoveCoords(null, myPosition, direction.UPLEFT).getEndPosition()).getTeamColor() != myTeamColor) {
                            moves.add(calculateMoveCoords(null, myPosition, direction.UPLEFT));
                        }
                    } else {
                        //no piece there, so we can add it
                        moves.add(calculateMoveCoords(null, myPosition, direction.UPLEFT));
                    }
                }
                break;
            case QUEEN:
                //myPosition = position on the board - NOT INDEXES, ROWS AND COLUMNS
                //edge of board check, then open square check

                //+ directions
                //up
                moves.addAll(getMoves(board, myPosition, direction.UP, true));
                //down
                moves.addAll(getMoves(board, myPosition, direction.DOWN, true));
                //left
                moves.addAll(getMoves(board, myPosition, direction.LEFT, true));
                //right
                moves.addAll(getMoves(board, myPosition, direction.RIGHT, true));

                //x directions
                //upright
                moves.addAll(getMoves(board, myPosition, direction.UPRIGHT, true));
                //downright
                moves.addAll(getMoves(board, myPosition, direction.DOWNRIGHT, true));
                //downleft
                moves.addAll(getMoves(board, myPosition, direction.DOWNLEFT, true));
                //upleft
                moves.addAll(getMoves(board, myPosition, direction.UPLEFT, true));
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
