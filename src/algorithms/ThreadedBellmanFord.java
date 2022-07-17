package algorithms;

import components.Node;
import components.Traversable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadedBellmanFord<T> {
    private final ThreadLocal<Queue<List<Node<T>>>> localQueue = ThreadLocal.withInitial(LinkedList::new);
    private final ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 15, 500, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * This method find ALL valid paths from between two given nodes.<br>
     * This method invokes the {@link components.TraversableMatrix#getNeighbors(Node)} method.<br>
     *
     * @param graph       The graph to traverse over.
     * @param source      The source node.
     * @param destination The destination node.
     * @return List of all valid paths.
     */
    public LinkedList<List<Node<T>>> getAllPaths(Traversable<T> graph, Node<T> source, Node<T> destination) {
        List<Node<T>> currentPath = new ArrayList<>(); // current path of each iteration
        LinkedList<List<Node<T>>> allPaths = new LinkedList<>();

        currentPath.add(source);
        localQueue.get().offer(currentPath);

        while (!localQueue.get().isEmpty()) {
            //retrieving the head path in the queue (FIFO)
            currentPath = localQueue.get().poll();

            assert currentPath != null;
            Node<T> lastNode = currentPath.get(currentPath.size() - 1);

            if (lastNode.equals(destination)) {
                allPaths.add(currentPath);
            }
            //getting all neighbors of the polled node from the current path
            Collection<Node<T>> neighborsOfLastNode = graph.getNeighbors(lastNode);

            for (Node<T> aNode : neighborsOfLastNode) {
                if (!currentPath.contains(aNode)) {
                    List<Node<T>> aPath = new ArrayList<>(currentPath);
                    aPath.add(aNode);
                    localQueue.get().offer(aPath);
                }
            }
        }
        localQueue.get().clear();
        //returning all valid paths from source to destination
        return allPaths;
    }

    /**
     * The method sums up the total weight of a given path.
     *
     * @param graph    The graph.
     * @param fullPath The path which weight is being summed up.
     * @return Total weight of a path.
     */
    public int getPathWeight(Traversable<T> graph, @NotNull List<Node<T>> fullPath) {
        int pathWeight = 0;
        for (Node<T> aNode : fullPath) {
            pathWeight += graph.getValue(aNode);
        }
        return pathWeight;
    }

    /**
     * This method find all the lightest path between two given nodes in a graph.<br>
     * The method invokes the {@link #getPathWeight(Traversable, List)} & {@link #getAllPaths(Traversable, Node, Node)} methods.<br>
     *
     * @param graph       The given graph.
     * @param source      The source node.
     * @param destination The destination node.
     * @return List of all lightest weights.
     */
    public LinkedList<List<Node<T>>> getLightestPath(Traversable<T> graph, Node<T> source, Node<T> destination) {
        //Using AtomicInteger because the value are being updated inside a Callable (More readable than 'synchronized').
        AtomicInteger currentWeight = new AtomicInteger();
        AtomicInteger currentMinWeight = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger finalMinWeight = new AtomicInteger(); //the minimal target weight.

        LinkedList<List<Node<T>>> allPaths = this.getAllPaths(graph, source, destination);
        LinkedList<List<Node<T>>> allLightestPaths = new LinkedList<>();

        LinkedList<Future<List<Node<T>>>> pathsCandidates = new LinkedList<>();
        LinkedList<List<Node<T>>> futureLightestPaths = new LinkedList<>();

        //iterating over all valid paths to eliminate longer paths
        for (List<Node<T>> aPath : allPaths) {
            Callable<List<Node<T>>> isPathLightest = () -> {
                readWriteLock.writeLock().lock();
                currentWeight.set(getPathWeight(graph, aPath));

                //if the current path is lighter than the previous path
                if (currentWeight.get() <= currentMinWeight.get()) {
                    currentMinWeight.set(currentWeight.get());
                    finalMinWeight.set(currentWeight.get());

                    readWriteLock.writeLock().unlock();

                    if (finalMinWeight.get() > currentMinWeight.get()) {
                        //updating the target weight
                        finalMinWeight.set(currentWeight.get());
                    }
                    return aPath;
                } else {
                    currentWeight.set(0);
                    readWriteLock.writeLock().unlock();
                    return null;
                }
            };
            Future<List<Node<T>>> futurePath = poolExecutor.submit(isPathLightest);
            pathsCandidates.add(futurePath);
        }

        //adding all candidates to separate list & ignoring null paths.
        for (Future<List<Node<T>>> pathCandidate : pathsCandidates) {
            try {
                if (pathCandidate.get() != null) {
                    futureLightestPaths.add(pathCandidate.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        //iterating over each path and checking if its weight equals to the target weight
        for (List<Node<T>> aPath : futureLightestPaths) {
            int tempWeight = 0;

            for (Node<T> aNode : aPath) //summing up the total weight of a single path
                tempWeight += graph.getValue(aNode);

            //if current path weight is the same as target weight
            if (tempWeight == finalMinWeight.get())
                allLightestPaths.add(aPath);
        }
        this.poolExecutor.shutdown();

        return allLightestPaths;
    }
}
