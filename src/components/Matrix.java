package components;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Base class for all matrices types.<br>
 * It creates a 2D NxN matrix, or NxM matrices.
 */
public class Matrix implements Serializable {
    private final int[][] baseMatrix;

    public Matrix(int[][] baseMatrix) {
        List<int[]> rowsList = new ArrayList<>();

        for (int[] row : baseMatrix) {
            rowsList.add(row.clone());
        }
        this.baseMatrix = rowsList.toArray(new int[0][]);
    }

    public int[][] getBaseMatrix() {
        return baseMatrix;
    }

    /**
     * Returns the value of index (x,y)
     *
     * @param index The given {@link Index}
     * @return Integer value of index (x,y)
     */
    public int getValue(@NotNull Index index) {
        return this.baseMatrix[index.getRow()][index.getColumn()];
    }

    /**
     * This method invokes the {@link #getDiagonalNeighbors(Index)} & {@link #getNonDiagonalNeighbors(Index)}<br>
     * methods in order to find all neighbors of a given index.
     *
     * @param selectedIndex The selected index
     * @return Collection of neighbor indices.
     */
    public Collection<Index> getNeighbors(final @NotNull Index selectedIndex) {
        List<Index> allNeighbors = new ArrayList<>();

        allNeighbors.addAll(getDiagonalNeighbors(selectedIndex));
        allNeighbors.addAll(getNonDiagonalNeighbors(selectedIndex));

        return allNeighbors;
    }

    /**
     * This method find all the NON-diagonal neighbors of a given index
     *
     * @param index The selected index
     * @return Collection of neighbor indices
     */
    public Collection<Index> getNonDiagonalNeighbors(final @NotNull Index index) {
        List<Index> neighbors = new ArrayList<>();

        //the dummyVariable is used to trigger an exception if index is out of bounds
        int dummyVariable = 0;

        try {//upper index
            dummyVariable = baseMatrix[index.getRow() - 1][index.getColumn()];
            neighbors.add(new Index(index.getRow() - 1, index.getColumn()));
        } catch (IndexOutOfBoundsException | IllegalArgumentException ignore) {
        }
        try {//lower index
            dummyVariable = baseMatrix[index.getRow() + 1][index.getColumn()];
            neighbors.add(new Index(index.getRow() + 1, index.getColumn()));
        } catch (IndexOutOfBoundsException | IllegalArgumentException ignore) {
        }
        try {//left index
            dummyVariable = baseMatrix[index.getRow()][index.getColumn() - 1];
            neighbors.add(new Index(index.getRow(), index.getColumn() - 1));
        } catch (IndexOutOfBoundsException | IllegalArgumentException ignore) {
        }
        try {//right index
            dummyVariable = baseMatrix[index.getRow()][index.getColumn() + 1];
            neighbors.add(new Index(index.getRow(), index.getColumn() + 1));
        } catch (IndexOutOfBoundsException | IllegalArgumentException ignore) {
        }
        return neighbors;
    }

    /**
     * This method finds all the diagonal neighbors of a given index.
     *
     * @param index The selected index
     * @return Collection of neighbor indices
     */
    public Collection<Index> getDiagonalNeighbors(final @NotNull Index index) {
        List<Index> neighbors = new ArrayList<>();

        //the dummyVariable is used to trigger an exception if index is out of bounds
        int dummyVariable;

        try {//upper left index
            dummyVariable = baseMatrix[index.getRow() - 1][index.getColumn() - 1];
            neighbors.add(new Index(index.getRow() - 1, index.getColumn() - 1));
        } catch (IndexOutOfBoundsException | IllegalArgumentException ignore) {
        }
        try {//lower right index
            dummyVariable = baseMatrix[index.getRow() + 1][index.getColumn() + 1];
            neighbors.add(new Index(index.getRow() + 1, index.getColumn() + 1));
        } catch (IndexOutOfBoundsException | IllegalArgumentException ignore) {
        }
        try {//upper right index
            dummyVariable = baseMatrix[index.getRow() - 1][index.getColumn() + 1];
            neighbors.add(new Index(index.getRow() - 1, index.getColumn() + 1));
        } catch (IndexOutOfBoundsException | IllegalArgumentException ignore) {
        }
        try {//lower left index
            dummyVariable = baseMatrix[index.getRow() + 1][index.getColumn() - 1];
            neighbors.add(new Index(index.getRow() + 1, index.getColumn() - 1));
        } catch (IndexOutOfBoundsException | IllegalArgumentException ignore) {
        }
        return neighbors;
    }

    /**
     * A function that iterating over a matrix and adds an index object<br>
     * to a list if the value in that index is 1.
     *
     * @return List of indices with value = 1
     */
    public List<Index> getIndicesOfOnes() {
        List<Index> onesList = new ArrayList<>();

        for (int i = 0; i < baseMatrix.length; i++) {
            for (int j = 0; j < baseMatrix.length; j++) {
                if (baseMatrix[i][j] == 1)
                    onesList.add(new Index(i, j));
            }
        }
        return onesList;
    }

    @Override
    public String toString() {
        //Using StringBuilder to create & return a String representation of the matrix.
        StringBuilder sb = new StringBuilder();

        for (int[] row : this.baseMatrix)
            sb.append(Arrays.toString(row)).append("\n");

        return sb.toString();
    }
}
