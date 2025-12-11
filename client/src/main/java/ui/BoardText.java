package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static chess.ChessPiece.PieceType.*;
import static java.lang.Math.abs;
import static ui.EscapeSequences.*;

public class BoardText {
    //POV of board
    public static Boolean blackPOV = null;

    //iterators
    private static int timesRowNameCalled = 0;
    private static int boardRow = 0;
    private static int timesPrintPieceCalled = 0;
    private static boolean isPieceBlack;//declared here because no pointers in java

    // Padded characters.
    public static final String WHITE_KING = " ♔ ";
    public static final String WHITE_QUEEN = " ♕ ";
    public static final String WHITE_BISHOP = " ♗ ";
    public static final String WHITE_KNIGHT = " ♘ ";
    public static final String WHITE_ROOK = " ♖ ";
    public static final String WHITE_PAWN = " ♙ ";
    public static final String BLACK_KING = " ♚ ";
    public static final String BLACK_QUEEN = " ♛ ";
    public static final String BLACK_BISHOP = " ♝ ";
    public static final String BLACK_KNIGHT = " ♞ ";
    public static final String BLACK_ROOK = " ♜ ";
    public static final String BLACK_PAWN = " ♟ ";
    public static final String EMPTY = " \u2003 ";
    public static final String LCOL_EMPTY = "  ";
    public static final String RCOL_EMPTY = "    ";
    public static final String A = " \u2003\u0061";
    public static final String B = " \u2003\u0062";
    public static final String C = " \u2003\u0063";
    public static final String D = " \u2003\u0064";
    public static final String E = " \u2003\u0065";
    public static final String F = " \u2003\u0066";
    public static final String G = " \u2003\u0067";
    public static final String H = " \u2003\u0068";

    public static final String SNOWMAN = " \u2603 ";

    public static ChessBoard board;

    public BoardText(ChessBoard board, ChessGame.TeamColor color) {
        BoardText.board = board;
        if (color == ChessGame.TeamColor.BLACK) {
            blackPOV = true;
        } else {
            blackPOV = false;
        }
        timesRowNameCalled = 0;
        boardRow = 0;
        timesPrintPieceCalled = 0;
        isPieceBlack = false;
    }

    public void printBoard(ChessBoard board) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        boardRow = 0;
        BoardText.board = board;
        out.print(ERASE_SCREEN);
        out.print("\n");
        printSquares(out, null, null);

        resetColor(out);
    }

    public void highlightMoves(ChessBoard board, Collection<ChessMove> yellows, ChessPosition position) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        boardRow = 0;
        BoardText.board = board;
        out.print(ERASE_SCREEN);
        out.print("\n");
        printSquares(out, yellows, position);

        resetColor(out);
    }

    private static void printColNames(PrintStream out) {
        resetColor(out);
        String[] normalColNames = { LCOL_EMPTY, A, B, C, D, E, F, G, H, RCOL_EMPTY};
        String [] reversedColNames = { LCOL_EMPTY, H, G, F, E, D, C, B, A, RCOL_EMPTY};
        String[] colNames = blackPOV ? reversedColNames : normalColNames;
        for (int boardCol = 0; boardCol < 10; ++boardCol) {
            printHeaderText(out, colNames[boardCol]);
        }
        out.println();
    }

    private static void printRowName(PrintStream out) {
        timesRowNameCalled++;
        resetColor(out);
        String[] normalRowNames = { " 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
        String [] reversedRowNames = { " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
        String[] rowNames =  blackPOV ? reversedRowNames : normalRowNames;
        if (timesRowNameCalled % 2 == 1) {
            boardRow++;
        }
        printHeaderText(out, rowNames[boardRow - 1]);
    }

    private static void printHeaderText(PrintStream out, String name) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);
        out.print(name);
        resetColor(out);
    }

    private static void printSquares(PrintStream out, Collection<ChessMove> yellows, ChessPosition position) {
        printColNames(out);
        for (int boardRow = 0; boardRow < 8; boardRow++) {
            for (int boardCol = 0; boardCol < 10; boardCol++) {
                if (boardCol == 0 || boardCol == 9) {
                    setWhite(out);
                    printRowName(out);
                    resetColor(out);
                } else {
                    setWhite(out);
                    String piece = calcPieceString(boardRow + 1, boardCol);
                    if (yellows == null) {
                        printPiece(out, piece, false);
                    } else {//highlight
                        int realRow = 8 - boardRow;
                        int realCol = boardCol;

                        // apply black POV flip
                        if (blackPOV) {
                            realRow = 9 - realRow;
                            realCol = 9 - realCol;
                        }

                        ChessPosition end = new ChessPosition(realRow, realCol);
                        if (movesContains(yellows, end)) {
                            printPiece(out, piece, true);
                        } else {
                            printPiece(out, piece, false);
                        }
                    }
                    resetColor(out);
                }
            }
            out.println();
        }
        printColNames(out);
    }

    private static boolean movesContains(Collection<ChessMove> squares, ChessPosition pos) {
        //just checks start and end position, no promotion piece checking
        for (ChessMove square : squares) {
            if (square.getEndPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    private static String calcPieceString(int row, int col) {
        isPieceBlack = false;
        //here we use chess notation for rows and columns
        //reverse the order of printing if blackPOV
        row = abs(row - 8) + 1;
        if (blackPOV) {
            row = abs(row - 8) + 1;
            col = abs(col - 8) + 1;
        }

        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if ((piece.getPieceType() == ROOK)) {
                    return WHITE_ROOK;
                } else if (piece.getPieceType() == KNIGHT) {
                    return WHITE_KNIGHT;
                } else if (piece.getPieceType() == BISHOP) {
                    return WHITE_BISHOP;
                } else if (piece.getPieceType() == QUEEN) {
                    return WHITE_QUEEN;
                } else if (piece.getPieceType() == KING) {
                    return WHITE_KING;
                } else if (piece.getPieceType() == PAWN) {
                    return WHITE_PAWN;
                }
            } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                isPieceBlack = true;
                if ((piece.getPieceType() == ROOK)) {
                    return BLACK_ROOK;
                } else if (piece.getPieceType() == KNIGHT) {
                    return BLACK_KNIGHT;
                } else if (piece.getPieceType() == BISHOP) {
                    return BLACK_BISHOP;
                } else if (piece.getPieceType() == QUEEN) {
                    return BLACK_QUEEN;
                } else if (piece.getPieceType() == KING) {
                    return BLACK_KING;
                } else if (piece.getPieceType() == PAWN) {
                    return BLACK_PAWN;
                }
            } else {
                return SNOWMAN;//shouldn't get here, but it's fun
            }
        }
        return EMPTY;
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void resetColor(PrintStream out) {
        out.print("\u001b[0m");
    }

    private static void printPiece(PrintStream out, String piece, boolean highlight) {
        timesPrintPieceCalled++;
        if (highlight) {
            out.print(SET_BG_COLOR_YELLOW);
        } else if ((timesPrintPieceCalled + boardRow) % 2 == 0) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
        } else {
            out.print(SET_BG_COLOR_DARK_GREEN);
        }
        if (isPieceBlack) {
            out.print(SET_TEXT_COLOR_BLACK);
        } else {
            out.print(SET_TEXT_COLOR_WHITE);
        }
        out.print(piece);
        setWhite(out);
    }
}