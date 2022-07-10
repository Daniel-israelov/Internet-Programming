package components;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class implements the Adapter pattern.<br>
 * The class adapts a matrix object to the functionality of the Graph interface.
 */
public class TraversableMatrix implements Traversable<Index>, Serializable {
    private final Matrix matrix;
    private Index source;
    private Index destination;

    public TraversableMatrix(@NotNull Matrix matrix) {
        this.matrix = matrix;
        this.source = new Index(0, 0);
    }

    public TraversableMatrix(@NotNull Matrix matrix, Index source, Index destination) {
        this.matrix = matrix;
        setSource(source);
        setDestination(destination);
    }

    public Matrix getMatrix() {
        return matrix;
    }

    @Override
    public Index getSourceIndex() {
        return source;
    }

    @Override
    public void setSource(@NotNull Index source) {
        if (validateIndex(source))
            this.source = source;
        else
            throw new IndexOutOfBoundsException("Index " + source + " is out of bounds");
    }

    @Override
    public Index getDestinationIndex() throws NullPointerException {
        if (this.destination == null) throw new NullPointerException("Destination Index is not initialized");
        return this.destination;
    }

    @Override
    public void setDestination(@NotNull Index destination) {
        if (validateIndex(destination))
            this.destination = destination;
        else
            throw new IndexOutOfBoundsException("Index " + destination + " is out of bounds");
    }

    @Override
    public Node<Index> getSourceNode() {
        return new Node<>(this.source);
    }

    @Override
    public Node<Index> getDestinationNode() {
        //by calling 'getDestinationIndex' we can trigger an exception if destination is null
        return new Node<>(getDestinationIndex());
    }

    @Override
    public int getValue(Node<Index> node) {
        return this.matrix.getValue(node.getData());
    }

    /**
     * This method checks if a given index is within the matrix boundaries
     *
     * @param index The index to validate
     * @return true if index within matrix boundaries, false otherwise
     */
    public boolean validateIndex(Index index) {
        return (index.getRow() >= 0 && index.getRow() < matrix.getBaseMatrix().length) &&
                (index.getColumn() >= 0 && index.getColumn() < matrix.getBaseMatrix()[0].length);
    }

    @Override
    public Node<Index> getRoot() {
        return new Node<>(source);
    }

    /**
     * This method finds all reachable nodes of a given node.
     *
     * @param node The starting node
     * @return List of reachable nodes or empty list if the value of given node is 0
     */
    @Override
    public Collection<Node<Index>> getReachableNodes(@NotNull Node<Index> node) {
        List<Node<Index>> reachableNodes = new ArrayList<>();

        //if node data is 0, it means that it can't reach other nodes, hence an empty list is returned
        if (matrix.getValue(node.getData()) == 1) {
            reachableNodes.add(node);

            for (Index neighbor : this.matrix.getNeighbors(node.getData())) {
                if (matrix.getValue(neighbor) == 1) {
                    reachableNodes.add(new Node<>(neighbor, node));
                }
            }
        }
        return reachableNodes;
    }

    @Override
    public Collection<Node<Index>> getNeighbors(@NotNull Node<Index> node) {
        List<Node<Index>> neighbors = new ArrayList<>();

        for (Index index : this.matrix.getNeighbors(node.getData())) {
            Node<Index> indexNode = new Node<>(index, node);
            neighbors.add(indexNode);
        }
        return neighbors;
    }

    @Override
    public String toString() {
        return "Source: " + source.toString() + "\n" +
                "Matrix:\n" + matrix.toString();
    }
}
