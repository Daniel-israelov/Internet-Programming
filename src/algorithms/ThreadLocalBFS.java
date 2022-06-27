package algorithms;

import components.Node;
import components.Traversable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

//-->>> DONE <<<--

/**
 * A class that implements the bfs algorithm to find all shortest paths of a graph.
 *
 * @param <T>
 */
public class ThreadLocalBFS<T> {
    //Using threadLocal so each client that requests the use of BFS class, will have his own values
    //corresponding to his thread
    private final ThreadLocal<LinkedList<LinkedList<Node<T>>>> localQueue = ThreadLocal.withInitial(LinkedList::new);

    /**
     * This method traverses over a graph and find all the paths with minimum length<br>
     * from source node to destination node using the BFS Algorithm.
     *
     * @param graph       The graph to traverse over.
     * @param source      The source node
     * @param destination The destination node
     * @return List of lists of all shortest paths.
     */
    public List<List<Node<T>>> getShortestPaths(Traversable<T> graph, Node<T> source, Node<T> destination) {
        int shortestPathLength = Integer.MAX_VALUE;

        //creating list of lists in case there multiple short path (with the same length)
        List<List<Node<T>>> allShortestPaths = new ArrayList<>();
        LinkedList<Node<T>> currentPath = new LinkedList<>();

        //source node is added as the head of the linked list.
        currentPath.add(source);
        localQueue.get().add(currentPath);

        while (!localQueue.get().isEmpty()) {
            //need to poll the current path from the LocalThread.
            currentPath = localQueue.get().poll();

            //retrieving the last element in the list WITHOUT removing it from the list
            assert currentPath != null;
            Node<T> lastNode = currentPath.getLast();

            /*
            If the last node in the list is the same as the source node,
            We can break the loop & return the lists of the shortest paths
             */
            if (lastNode.equals(destination)) {
                if (shortestPathLength < currentPath.size())
                    break;
                else {
                    //setting a new shortest length
                    shortestPathLength = currentPath.size();
                    allShortestPaths.add(currentPath);
                }
            }

            Collection<Node<T>> reachableNodes = graph.getReachableNodes(lastNode);

            //iterating over the neighbors of the current node to find next step (node) options.
            for (Node<T> aNode : reachableNodes) {
                if (!currentPath.contains(aNode)) {
                    LinkedList<Node<T>> aPath = new LinkedList<>(currentPath);
                    aPath.add(aNode);
                    localQueue.get().add(aPath);
                }
            }
        }
        localQueue.get().clear();
        return allShortestPaths;
    }
}
