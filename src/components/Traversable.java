package components;

import java.util.Collection;

public interface Traversable<T> {
    Node<T> getRoot();

    Collection<Node<T>> getReachableNodes(Node<T> node);

    void setSource(Index index);

    Index getSourceIndex();

    Node<Index> getSourceNode();

    void setDestination(Index index);

    Index getDestinationIndex();

    Node<Index> getDestinationNode();

    int getValue(Node<T> node);
}
