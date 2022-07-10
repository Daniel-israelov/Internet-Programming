package clientserver;

import algorithms.SubmarineValidator;
import algorithms.ThreadedBFS;
import algorithms.ThreadedBellmanFord;
import algorithms.ThreadedDFS;
import components.Index;
import components.Matrix;
import components.Node;
import components.TraversableMatrix;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
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
                        List<HashSet<Index>> allStronglyConnectedComponents = new ThreadedDFS<Index>().stronglyConnectedComponents(this.matrix);
                        clientOutputStream.writeObject(allStronglyConnectedComponents);
                    }
                    case "shortest path" -> {
                        this.matrix = new Matrix((int[][]) clientInputStream.readObject());
                        this.source = (Index) clientInputStream.readObject();
                        this.destination = (Index) clientInputStream.readObject();

                        TraversableMatrix graph = new TraversableMatrix(this.matrix);
                        graph.setSource(this.source);
                        graph.setDestination(this.destination);

                        ThreadedBFS<Index> bfs = new ThreadedBFS<>();
                        List<List<Node<Index>>> shortestPaths = bfs.getShortestPaths(graph, graph.getSourceNode(), graph.getDestinationNode());
                        clientOutputStream.writeObject(shortestPaths);
                    }
                    case "find submarines" -> {
                        int[][] baseMatrix = (int[][]) clientInputStream.readObject();
                        this.matrix = new Matrix(baseMatrix);

                        ThreadedDFS<Index> dfs = new ThreadedDFS<>();
                        List<HashSet<Index>> allConnectedComponents = dfs.stronglyConnectedComponents(this.matrix);
                        int submarinesCount = new SubmarineValidator().findSubmarinesAmount(allConnectedComponents, baseMatrix);
                        clientOutputStream.writeObject(submarinesCount);
                    }
                    case "shortest path weighted graph" -> {
                        this.matrix = new Matrix((int[][]) clientInputStream.readObject());
                        this.source = (Index) clientInputStream.readObject();
                        this.destination = (Index) clientInputStream.readObject();

                        TraversableMatrix weightedGraph = new TraversableMatrix(this.matrix);
                        weightedGraph.setSource(this.source);
                        weightedGraph.setDestination(this.destination);

                        ThreadedBellmanFord<Index> bellmanFord = new ThreadedBellmanFord<>();
                        LinkedList<List<Node<Index>>> allLightestPaths = bellmanFord.getLightestPath(weightedGraph, weightedGraph.getSourceNode(), weightedGraph.getDestinationNode());
                        clientOutputStream.writeObject(allLightestPaths);
                    }
                    case "stop" -> activeSession = false;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
