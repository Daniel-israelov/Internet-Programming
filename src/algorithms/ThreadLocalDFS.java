package algorithms;

import components.Node;
import components.Traversable;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadLocalDFS<T> implements Serializable {
    private final ThreadLocal<Stack<Node<T>>> localStack = ThreadLocal.withInitial((Stack::new));
    //LinkedHashSet keeps the insertion order
    private final ThreadLocal<Set<Node<T>>> localSet = ThreadLocal.withInitial((LinkedHashSet::new));

    //pool to handle multiple incoming requests
    public ThreadPoolExecutor requestsPool = new ThreadPoolExecutor(5, 15, 500, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());


    /**
     * Locally traversing over a graph to get all the connected components<br>
     * from the origin node of the given graph.
     *
     * @param graph The graph to traverse.
     * @return A set of connected components.
     */
    public Set<T> localTraverse(@NotNull Traversable<T> graph) {
        //inserting the graph's root node to the working (local) stack
        localStack.get().push(graph.getRoot());

        while (!localStack.get().isEmpty()) {
            Node<T> poppedNode = localStack.get().pop();
            localSet.get().add(poppedNode);

            Collection<Node<T>> reachableNodes = graph.getReachableNodes(poppedNode);
            for (Node<T> aNode : reachableNodes) {
                if (!localStack.get().contains(aNode) && !localSet.get().contains(aNode))
                    localStack.get().push(aNode);
            }
        }

        //Using a Set to make sure there are no duplicated nodes
        Set<T> connectedComponent = new HashSet<>();
        localSet.get().forEach(node -> connectedComponent.add(node.getData()));

        localStack.get().clear();
        localSet.get().clear();

        return connectedComponent;
    }
}
