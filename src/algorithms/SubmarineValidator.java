package algorithms;

import components.Index;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

/**
 * This class checks how many submarines in a given matrix (2D array)
 */
public class SubmarineValidator {
    /**
     * The method check how many submarines in a 2D array.
     *
     * @param connectedComponents List of all connected components of the given 2D array.
     * @param baseMatrix          The basic 2D array given by user.
     * @return Amount of valid Submarines in a 2D array.
     */
    public int findSubmarinesAmount(@NotNull List<HashSet<Index>> connectedComponents, int[][] baseMatrix) {
        int submarinesCounter = connectedComponents.size();
        int minRow, minCol, maxRow, maxCol;

        for (HashSet<Index> connectedComponent : connectedComponents) {
            minRow = minCol = Integer.MAX_VALUE;
            maxRow = maxCol = 0;

            //if a set size is 1, then it's not a valid submarine.
            if (connectedComponent.size() == 1) {
                submarinesCounter--;
                continue;
            }

            //setting the boundaries to iterate over and check if it contains a submarine
            for (Index index : connectedComponent) {
                minRow = Math.min(minRow, index.getRow());
                minCol = Math.min(minCol, index.getColumn());
                maxRow = Math.max(maxRow, index.getRow());
                maxCol = Math.max(maxCol, index.getColumn());
            }

            //iterating over the boundaries of a suspected submarine
            for (int i = minRow; i <= maxRow; i++) {
                for (int j = minCol; j <= maxCol; j++) {
                    if (baseMatrix[i][j] == 0) {
                        submarinesCounter--;
                        i = maxRow;
                        break;
                    }
                }
            }
        }
        return submarinesCounter;
    }
}
