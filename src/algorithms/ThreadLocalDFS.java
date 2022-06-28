package algorithms;

import components.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

//-->>> DONE <<<--

/**
 * The class represents a DFS algorithm to traverse over a graph.
 */
public class ThreadLocalDFS<T> implements Serializable {
    private final ThreadLocal<Stack<Node<T>>> localStack = ThreadLocal.withInitial((Stack::new));
    //LinkedHashSet keeps the insertion order
    private final ThreadLocal<Set<Node<T>>> localSet = ThreadLocal.withInitial((LinkedHashSet::new));

    //pool to handle multiple incoming requests
    private final ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 15, 500, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    //using read-write lock to prevent the 'reader-Writer problem'
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * The method performs parallel traversing of a given graph.<br>
     * Each iteration traverses the graph from a different node as a source node.<br>
     * The method invokes the {@link #localTraverse(Traversable)} method.
     *
     * @param graph         The graph to traverse.
     * @param indicesOfOnes List of valid indices (value=1) to traverse from.
     * @return HashSet of all connected components.
     */
    public HashSet<HashSet<T>> parallelTraverse(Traversable<T> graph, @NotNull List<Index> indicesOfOnes) {
        //using the Parallel approach in order to 'cover' more area simultaneously and save runtime.
        HashSet<Future<HashSet<T>>> futureSetOfConnectedComponents = new HashSet<>();
        HashSet<HashSet<T>> indicesSet = new HashSet<>();

        //iterating over all valid indices
        for (int i = 0; i < indicesOfOnes.size(); i++) {
            int finalI = i;

            Callable<HashSet<T>> currentNodeTraversal = () -> {
                //locking the write option so another thread won't be able to use the same index object
                readWriteLock.writeLock().lock();

                //setting new source on each iteration
                graph.setSource(indicesOfOnes.get(finalI));

                //executing the local traverse on the graph with the current node as source
                HashSet<Index> connectedComponent = (HashSet<Index>) localTraverse(graph);
                readWriteLock.writeLock().unlock();
                return (HashSet<T>) connectedComponent;
            };

            //submitting the callable task to the pool and moving on to the next index (node).
            //using Future because we need to store the result of asynchronous operation.
            Future<HashSet<T>> futureSet = poolExecutor.submit(currentNodeTraversal);
            futureSetOfConnectedComponents.add(futureSet);
        }

        //trying to add a single component to the set of all connected components.
        for (Future<HashSet<T>> futureComponent : futureSetOfConnectedComponents) {
            try {
                indicesSet.add(futureComponent.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        this.poolExecutor.shutdown();
        return indicesSet;
    }

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
                if (!localStack.get().contains(aNode) && !localSet.get().contains(aNode)) {
                    localStack.get().push(aNode);
                }
            }
        }
        //Using a Set to ensure there are no duplicated nodes
        Set<T> connectedComponent = new HashSet<>();
        localSet.get().forEach(node -> connectedComponent.add(node.getData()));

        localStack.get().clear();
        localSet.get().clear();

        return connectedComponent;
    }

    /**
     * This method is used to find all Strongly connected components of a graph.<br>
     * The method invokes the {@link #parallelTraverse(Traversable, List)} method.
     *
     * @param matrix The given matrix (which is converted to {@link TraversableMatrix} inside the method).
     * @return List of hash sets of strongly connected components, sorted by set size
     */
    public List<HashSet<Index>> stronglyConnectedComponents(@NotNull Matrix matrix) {
        List<Index> indicesOfOnes = matrix.getIndicesOfOnes();
        TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);
        ThreadLocalDFS<Index> dfs = new ThreadLocalDFS<>();
        HashSet<HashSet<Index>> allConnectedComponents = dfs.parallelTraverse(traversableMatrix, indicesOfOnes);

        //returning a size-wise sorted set of connected components
        return allConnectedComponents.stream().sorted(Comparator.comparing(HashSet::size)).collect(Collectors.toList());
    }
}
