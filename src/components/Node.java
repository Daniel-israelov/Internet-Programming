package components;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents a Node in a Graph.
 */
public class Node<T> implements Serializable {
    private T data;
    private Node<T> parent;

    /**
     * A constructor for a Node and his parent.
     *
     * @param data   The current node
     * @param parent The parent of the current node
     */
    public Node(T data, Node<T> parent) {
        this.data = data;
        this.parent = parent;
    }

    /**
     * Constructor that initializes the parent Node.
     *
     * @param data Parent node
     */
    public Node(T data) {
        this(data, null);
    }

    public Node() {
        this(null);
    }

    public T getData() {
        return data;
    }

    public void setData(@NotNull T data) {
        this.data = data;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void setParent(@NotNull Node<T> parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node<?> aNode)) return false;
        return Objects.equals(this.data, aNode.data);
    }

    @Override
    public int hashCode() {
        return this.data != null ? data.hashCode() : 0;
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
