package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private ArrayList<ChessPiece> capturablePieces;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.capturablePieces = new ArrayList<ChessPiece>();
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

    public ArrayList<ChessPiece> getCapturablePieces(ChessBoard board, ChessPosition position) {
        resetCapturablePieces();
        pieceMoves(board, position);
        return capturablePieces;
    }

    public void resetCapturablePieces() {
        capturablePieces = new ArrayList<ChessPiece>();
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    boolean coordsOnBoard(ChessPosition position) {
        //first check
        int row  = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
    boolean blocked(ChessBoard board, ChessPosition square) {
        //second check, assumes it's on the board
        if (board.getPiece(square) == null) {
            return false;
        } else {
            return true;
        }
    }
    boolean capturable(ChessPiece myPiece, ChessPiece prospectPiece) {
        //3rd check, assumes its on the board and blocked
        ChessGame.TeamColor myTeamColor = myPiece.getTeamColor();
        ChessGame.TeamColor prospectPieceTeamColor = prospectPiece.getTeamColor();
        if (prospectPieceTeamColor != myTeamColor) {
            //add to list of capturable pieces
            capturablePieces.add(prospectPiece);
            return true;
        } else {
            return false;
        }
    }

    ArrayList<ChessMove> plusMoves(ChessBoard board, ChessPosition myPosition, boolean recursive) {
        ArrayList<ChessMove> plusMoves = new ArrayList<>();
        //UP
        ChessPosition endCoords = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        while (coordsOnBoard(endCoords)) {
            if (blocked(board, endCoords)) {
                if (capturable(board.getPiece(myPosition), board.getPiece(endCoords))) {
                    plusMoves.add(new ChessMove(myPosition, endCoords, null));
                }
                break;
            } else {
                plusMoves.add(new ChessMove(myPosition, endCoords, null));
                if (recursive) {
                    endCoords = new ChessPosition(endCoords.getRow() + 1, endCoords.getColumn());
                } else {
                    break;
                }
            }
        }
        //RIGHT
        endCoords = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
        while (coordsOnBoard(endCoords)) {
            if (blocked(board, endCoords)) {
                if (capturable(board.getPiece(myPosition), board.getPiece(endCoords))) {
                    plusMoves.add(new ChessMove(myPosition, endCoords, null));
                }
                break;
            } else {
                plusMoves.add(new ChessMove(myPosition, endCoords, null));
                if (recursive) {
                    endCoords = new ChessPosition(endCoords.getRow(), endCoords.getColumn() + 1);
                } else {
                    break;
                }
            }
        }
        //DOWN
        endCoords = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        while (coordsOnBoard(endCoords)) {
            if (blocked(board, endCoords)) {
                if (capturable(board.getPiece(myPosition), board.getPiece(endCoords))) {
                    plusMoves.add(new ChessMove(myPosition, endCoords, null));
                }
                break;
            } else {
                plusMoves.add(new ChessMove(myPosition, endCoords, null));
                if (recursive) {
                    endCoords = new ChessPosition(endCoords.getRow() - 1, endCoords.getColumn());
                } else {
                    break;
                }
            }
        }
        //LEFT
        endCoords = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
        while (coordsOnBoard(endCoords)) {
            if (blocked(board, endCoords)) {
                if (capturable(board.getPiece(myPosition), board.getPiece(endCoords))) {
                    plusMoves.add(new ChessMove(myPosition, endCoords, null));
                }
                break;
            } else {
                plusMoves.add(new ChessMove(myPosition, endCoords, null));
                if (recursive) {
                    endCoords = new ChessPosition(endCoords.getRow(), endCoords.getColumn() - 1);
                } else {
                    break;
                }
            }
        }
        return plusMoves;
    }
    ArrayList<ChessMove> multiplyMoves(ChessBoard board, ChessPosition myPosition, boolean recursive) {
        ArrayList<ChessMove> multiplyMoves = new ArrayList<>();
        //UPRIGHT
        ChessPosition endCoords = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
        while (coordsOnBoard(endCoords)) {
            if (blocked(board, endCoords)) {
                if (capturable(board.getPiece(myPosition), board.getPiece(endCoords))) {
                    multiplyMoves.add(new ChessMove(myPosition, endCoords, null));
                }
                break;
            } else {
                multiplyMoves.add(new ChessMove(myPosition, endCoords, null));
                if (recursive) {
                    endCoords = new ChessPosition(endCoords.getRow() + 1, endCoords.getColumn() + 1);
                } else {
                    break;
                }
            }
        }
        //DOWNRIGHT
        endCoords = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
        while (coordsOnBoard(endCoords)) {
            if (blocked(board, endCoords)) {
                if (capturable(board.getPiece(myPosition), board.getPiece(endCoords))) {
                    multiplyMoves.add(new ChessMove(myPosition, endCoords, null));
                }
                break;
            } else {
                multiplyMoves.add(new ChessMove(myPosition, endCoords, null));
                if (recursive) {
                    endCoords = new ChessPosition(endCoords.getRow() - 1, endCoords.getColumn() + 1);
                } else {
                    break;
                }
            }
        }
        //DOWNLEFT
        endCoords = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
        while (coordsOnBoard(endCoords)) {
            if (blocked(board, endCoords)) {
                if (capturable(board.getPiece(myPosition), board.getPiece(endCoords))) {
                    multiplyMoves.add(new ChessMove(myPosition, endCoords, null));
                }
                break;
            } else {
                multiplyMoves.add(new ChessMove(myPosition, endCoords, null));
                if (recursive) {
                    endCoords = new ChessPosition(endCoords.getRow() - 1, endCoords.getColumn() - 1);
                } else {
                    break;
                }
            }
        }
        //UPLEFT
        endCoords = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
        while (coordsOnBoard(endCoords)) {
            if (blocked(board, endCoords)) {
                if (capturable(board.getPiece(myPosition), board.getPiece(endCoords))) {
                    multiplyMoves.add(new ChessMove(myPosition, endCoords, null));
                }
                break;
            } else {
                multiplyMoves.add(new ChessMove(myPosition, endCoords, null));
                if (recursive) {
                    endCoords = new ChessPosition(endCoords.getRow() + 1, endCoords.getColumn() - 1);
                } else {
                    break;
                }
            }
        }
        return multiplyMoves;
    }
    ArrayList<ChessMove> promotableMoves(ChessPosition myPosition, ChessPosition endPos) {
        ArrayList<ChessMove> promotableMoves = new ArrayList<>();
        promotableMoves.add(new ChessMove(myPosition, endPos, PieceType.QUEEN));
        promotableMoves.add(new ChessMove(myPosition, endPos, PieceType.KNIGHT));
        promotableMoves.add(new ChessMove(myPosition, endPos, PieceType.ROOK));
        promotableMoves.add(new ChessMove(myPosition, endPos, PieceType.BISHOP));
        return promotableMoves;
    }

    ArrayList<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> kingMoves = new ArrayList<>();
        //+ moves
        kingMoves.addAll(plusMoves(board, myPosition, false));
        //x moves
        kingMoves.addAll(multiplyMoves(board, myPosition, false));
        return kingMoves;
    }
    ArrayList<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> queenMoves = new ArrayList<>();
        //+ moves
        queenMoves.addAll(plusMoves(board, myPosition, true));
        //x moves
        queenMoves.addAll(multiplyMoves(board, myPosition, true));
        return queenMoves;
    }
    //bishop and rook done inline
    ArrayList<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> knightMoves = new ArrayList<>();
        ChessPosition endCoords = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        for (int i = 0; i < 8; i++) {
            if  (i == 0) {
                //UPRIGHT
                endCoords = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1);
            } else if  (i == 1) {
                //RIGHTUP
                endCoords = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2);
            } else if  (i == 2) {
                //RIGHTDOWN
                endCoords = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2);
            } else if  (i == 3) {
                //DOWNRIGHT
                endCoords = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1);
            } else if  (i == 4) {
                //DOWNLEFT
                endCoords = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1);
            } else if  (i == 5) {
                //LEFTDOWN
                endCoords = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2);
            } else if  (i == 6) {
                //LEFTUP
                endCoords = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2);
            } else if  (i == 7) {
                //UPLEFT
                endCoords = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1);
            } else {
                System.out.println("ERROR IN KNIGHT MOVES");
            }
            if (coordsOnBoard(endCoords)) {
                if (blocked(board, endCoords)) {
                    if (capturable(board.getPiece(myPosition), board.getPiece(endCoords))) {
                        knightMoves.add(new ChessMove(myPosition, endCoords, null));
                    }
                } else {
                    knightMoves.add(new ChessMove(myPosition, endCoords, null));
                }
            }

        }

        return knightMoves;
    }
    ArrayList<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> pawnMoves = new ArrayList<>();
        ChessGame.TeamColor myTeam = board.getPiece(myPosition).getTeamColor();
        if (myTeam == ChessGame.TeamColor.WHITE) {
            //1 in front
            ChessPosition endPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            if (coordsOnBoard(endPos)) {
                if (!blocked(board, endPos)) {//promotion
                    if (myPosition.getRow() == 7) {
                        pawnMoves.addAll(promotableMoves(myPosition, endPos));
                    } else {
                        pawnMoves.add(new ChessMove(myPosition, endPos, null));
                    }
                }
            }
            //2 in front
            if (myPosition.getRow() == 2) { //starting position, we know its on the board
                endPos = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                if (!blocked(board, endPos) && !pawnMoves.isEmpty()) {//checks if the first check populated the arraylist
                    pawnMoves.add(new ChessMove(myPosition, endPos, null));
                }
            }
            //diagonals
            endPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
            if (coordsOnBoard(endPos)) {
                if (blocked(board, endPos)) {
                    if (capturable(board.getPiece(myPosition), board.getPiece(endPos))) {//promotion
                        if (myPosition.getRow() == 7) {
                            pawnMoves.addAll(promotableMoves(myPosition, endPos));
                        } else {
                            pawnMoves.add(new ChessMove(myPosition, endPos, null));
                        }
                    }
                }
            }
            endPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
            if (coordsOnBoard(endPos)) {
                if (blocked(board, endPos)) {
                    if (capturable(board.getPiece(myPosition), board.getPiece(endPos))) {//promotion
                        if (myPosition.getRow() == 7) {
                            pawnMoves.addAll(promotableMoves(myPosition, endPos));
                        } else {
                            pawnMoves.add(new ChessMove(myPosition, endPos, null));
                        }
                    }
                }
            }
        } else {
            //BLACK
            //1 in front
            ChessPosition endPos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            if (coordsOnBoard(endPos)) {
                if (!blocked(board, endPos)) {//promotion
                    if (myPosition.getRow() == 2) {
                        pawnMoves.addAll(promotableMoves(myPosition, endPos));
                    } else {
                        pawnMoves.add(new ChessMove(myPosition, endPos, null));
                    }
                }
            }
            //2 in front
            if (myPosition.getRow() == 7) { //starting position, we know its on the board
                endPos = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                if (!blocked(board, endPos) && !pawnMoves.isEmpty()) {//checks if the first check populated the arraylist
                    pawnMoves.add(new ChessMove(myPosition, endPos, null));
                }
            }
            //diagonals
            endPos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
            if (coordsOnBoard(endPos)) {
                if (blocked(board, endPos)) {
                    if (capturable(board.getPiece(myPosition), board.getPiece(endPos))) {//promotion
                        if (myPosition.getRow() == 2) {
                            pawnMoves.addAll(promotableMoves(myPosition, endPos));
                        } else {
                            pawnMoves.add(new ChessMove(myPosition, endPos, null));
                        }
                    }
                }
            }
            endPos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
            if (coordsOnBoard(endPos)) {
                if (blocked(board, endPos)) {
                    if (capturable(board.getPiece(myPosition), board.getPiece(endPos))) {//promotion
                        if (myPosition.getRow() == 2) {
                            pawnMoves.addAll(promotableMoves(myPosition, endPos));
                        } else {
                            pawnMoves.add(new ChessMove(myPosition, endPos, null));
                        }
                    }
                }
            }
        }
        return pawnMoves;
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
        ChessPiece myPiece = board.getPiece(myPosition);
        PieceType myPieceType = myPiece.getPieceType();
        switch (myPieceType) {
            case KING:
                moves.addAll(kingMoves(board, myPosition));
                break;
            case QUEEN:
                moves.addAll(queenMoves(board, myPosition));
                break;
            case BISHOP:
                moves.addAll(multiplyMoves(board, myPosition, true));
                break;
            case ROOK:
                moves.addAll(plusMoves(board, myPosition, true));
                break;
            case KNIGHT:
                moves.addAll(knightMoves(board, myPosition));
                break;
            case PAWN:
                moves.addAll(pawnMoves(board, myPosition));
                break;
            default:
                System.out.println("Unknown piece type: " + myPieceType);
        }
        System.out.println(board.toString());
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        String piece = "asdf";
        if (type == ChessPiece.PieceType.KING) {
            piece = "k";
        } else if (type == ChessPiece.PieceType.QUEEN) {
            piece = "q";
        } else if (type == ChessPiece.PieceType.BISHOP) {
            piece = "b";
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            piece = "n";
        } else if (type == ChessPiece.PieceType.ROOK) {
            piece = "r";
        } else if (type == ChessPiece.PieceType.PAWN) {
            piece = "p";
        } else {
            piece = "a";
        }
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            return piece.toLowerCase();
        } else {
            return piece.toUpperCase();
        }
    }
}
