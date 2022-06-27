package clientserver;

import algorithms.ThreadLocalBFS;
import components.Index;
import components.Matrix;
import components.Node;
import components.TraversableMatrix;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * The class handles Matrix-related tasks.<br>
 * The class implements the {@link IHandler} interface.
 */
public class MatrixHandler implements IHandler {
    private Matrix matrix;
    private Index source;
    private Index destination;
    private boolean activeSession;

    public MatrixHandler() {
        this.activeSession = true;
    }

    @Override
    public void resetClassFields() {
        this.matrix = null;
        this.source = null;
        this.destination = null;
        this.activeSession = true;
    }

    @Override
    public void handleClient(InputStream fromClient, OutputStream toClient) {
        resetClassFields();

        try {
            ObjectInputStream clientInputStream = new ObjectInputStream(fromClient);
            ObjectOutputStream clientOutputStream = new ObjectOutputStream(toClient);

            while (activeSession) {
                switch (clientInputStream.readObject().toString()) {
                    case "all reachable nodes" -> {
                        this.matrix = new Matrix((int[][]) clientInputStream.readObject());
                        TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);

                        //list of indices where data=1
                        HashSet<Index> indicesOfOnes = matrix.getIndicesOfOnes();

                        //list of lists that holds the list of reachable for each node data=1
                        List<List<Node<Index>>> allReachableNodes = new ArrayList<>();

                        //loop that creates list of reachable nodes from Index i
                        //and adds it to the list of all reachable nodes
                        for (Index i : indicesOfOnes) {
                            List<Node<Index>> currentReachableNodes = new ArrayList<>(traversableMatrix.getReachableNodes(new Node<>(i)));

                            //sorting each list of neighbors in ascending order
                            currentReachableNodes.sort(Comparator.comparing(Node::getData));
                            allReachableNodes.add(currentReachableNodes);
                        }
                        //sorting by list size in ascending order
                        allReachableNodes.sort(Comparator.comparingInt(List::size));
                        clientOutputStream.writeObject(allReachableNodes);
                    }
                    case "shortest path" -> {
                        this.matrix = new Matrix((int[][]) clientInputStream.readObject());
                        this.source = (Index) clientInputStream.readObject();
                        this.destination = (Index) clientInputStream.readObject();
                        TraversableMatrix graph = new TraversableMatrix(this.matrix);
                        graph.setSource(this.source);
                        graph.setDestination(this.destination);

                        ThreadLocalBFS bfs = new ThreadLocalBFS();
                        List<List<Index>> shortestPaths = bfs.getShortestPaths(graph, graph.getSourceNode(), graph.getDestinationNode());
                        clientOutputStream.writeObject(shortestPaths);
                    }
                    case "find submarines" -> {
                        this.matrix = new Matrix((int[][]) clientInputStream.readObject());

                    }
                    case "shortest path weighted graph" -> {

                    }
                    case "stop" -> activeSession = false;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
