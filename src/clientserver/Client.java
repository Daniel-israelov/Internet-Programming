package clientserver;

import components.Index;
import components.Matrix;
import components.Node;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;

public class Client {
    static Scanner in = new Scanner(System.in);

    private static @NotNull String menu() {
        System.out.println("\nSelect option from the following:");
        System.out.println("1. Find all reachable nodes.");
        System.out.println("2. Find shortest path from A to B.");
        System.out.println("3. Find the amount of submarines.");
        System.out.println("4. Find shortest path of weighted graph.");
        System.out.println("0. Stop and exit.");

        System.out.print("Your choice: ");
        switch (in.nextInt()) {
            case 1 -> {
                return "all reachable nodes";
            }
            case 2 -> {
                return "shortest path";
            }
            case 3 -> {
                return "find submarines";
            }
            case 4 -> {
                return "shortest path weighted graph";
            }
            case 0 -> {
                return "stop";
            }
        }
        return "invalid";
    }

    /**
     * The method validates the user inputs for index object.
     *
     * @param matrixSize The matrix size is an indication for maximum valid value for row & column.
     * @param row        The row input by user.
     * @param column     The column input by user.
     * @return true if inputs are valid, false if at least 1 of them is not valid.
     */
    private static boolean validateIndex(int matrixSize, int row, int column) {
        boolean isValidRow = row >= 0 && row < matrixSize;
        boolean isValidColumn = column >= 0 && column < matrixSize;
        return isValidColumn && isValidRow;
    }

    /**
     * This method get user input and creates a new {@link Index} object.<br>
     * The method invokes the {@link #validateIndex} method.
     *
     * @param matrix    The matrix that is used to validate the index.
     * @param indexType A string to let user know what is the index meant for.
     * @return {@link Index} object with the user inputs.
     */
    private static Index createIndex(Matrix matrix, String indexType) {
        System.out.println("Enter row and column values for " + indexType + ":");
        System.out.print("Row: ");
        int row = in.nextInt();
        System.out.print("Column: ");
        int column = in.nextInt();

        while (!validateIndex(matrix.getBaseMatrix().length, row, column)) {
            System.out.println("Invalid values, try again!");
            System.out.print("Row: ");
            row = in.nextInt();
            System.out.print("Column: ");
            column = in.nextInt();
        }
        return new Index(row, column);
    }


    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8010);
            System.out.println("Client side ready.");

            //output stream should be declared before input stream
            ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
            /*
            ToDo -> create some matrices here
             */
            int[][] input1 = {
                    {1, 0, 0},
                    {1, 1, 0},
                    {1, 1, 0}
            };

            int[][] input2 = {
                    {1, 0, 0},
                    {1, 1, 0},
                    {1, 1, 0}
            };

            boolean clientsConnection = true;
            //while clients connected and sending requests to the server
            while (clientsConnection) {
                String clientRequest = menu();
                System.out.println();

                switch (clientRequest) {
                    case "all reachable nodes" -> {
                        toServer.writeObject(clientRequest);
                        toServer.writeObject(input1);
                        LinkedHashSet<List<Node<Index>>> allReachableNodes = new LinkedHashSet<>((List<List<Node<Index>>>) fromServer.readObject());
                        System.out.println("All Reachable Nodes:");
                        allReachableNodes.forEach(System.out::println);
                    }
                    case "shortest path" -> {
                        toServer.writeObject("shortest path");
                        toServer.writeObject(input2);
                        Matrix matrix = new Matrix(input2);
                        Index source = createIndex(matrix, "Source");
                        Index destination = createIndex(matrix, "Destination");
                        toServer.writeObject(source);
                        toServer.writeObject(destination);
                        List<List<Index>> shortestPaths = new ArrayList<>((List<List<Index>>) fromServer.readObject());
                        System.out.println("All Shortest path from " + source + " to " + destination + ":");
                        shortestPaths.forEach(System.out::println);
                    }
                    case "find submarines" -> {

                    }
                    case "shortest path weighted graph" -> {

                    }
                    case "stop" -> {
                        clientsConnection = false;
                        toServer.writeObject("stop");
                        fromServer.close();
                        toServer.close();
                        socket.close();
                    }
                    case "invalid" -> System.out.println("Invalid choice, please try again.\n");
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
