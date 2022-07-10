package components;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 * This class represents Index of a matrix.
 */
public class Index implements Serializable, Comparable<Index> {
    private int row;
    private int column;

    public Index(int row, int column) {
        //calling for setters because the setters can validate the values
        setRow(row);
        setColumn(column);
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        if (row < 0) throw new IllegalArgumentException("Row index can't be negative");
        else this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        if (column < 0) throw new IllegalArgumentException("Column index can't be negative");
        else this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Index index)) return false;
        return row == index.row && column == index.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }

    /**
     * Comparing indices by both ROW and COLUMN values.<br>
     * For example:
     * (1,2),(1,0) will turn to (1,0),(1,2)
     *
     * @param otherIndex the compared index
     * @return -1 if (this < otherIndex)<br>
     * 1 if (this > otherIndex)<br>
     * 0 otherwise
     */
    @Override
    public int compareTo(@NotNull Index otherIndex) {
        Comparator<Index> indexComparator = Comparator.comparing(Index::getRow).thenComparing(Index::getColumn);
        return indexComparator.compare(this, otherIndex);
    }
}
