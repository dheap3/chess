package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row, col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public String toString() {
        // Convert column number (1–8) into a letter (a–h)
        String[] files = {"a","b","c","d","e","f","g","h"};
        String file = files[col - 1];

        // Row is already the correct number
        int rank = row;

        return file + rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        int result = row * 7 + col * 31;
        return result;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }
}
