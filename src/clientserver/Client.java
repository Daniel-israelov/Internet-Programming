package clientserver;

import components.Index;
import components.Matrix;
import components.Node;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class Client {
    static Scanner in = new Scanner(System.in);

    public static void printMatrix(int[][] matrix) {
        System.out.println("Current Matrix:");

        for (int[] row : matrix)
            System.out.println(Arrays.toString(row));
        System.out.println();
    }

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
    private static Index createIndex(@NotNull Matrix matrix, String indexType) {
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

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8010);
            System.out.println("Client side ready.");

            //output stream should be declared before input stream
            ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

            //inputs for Task 1 - Connected components
            int[][] input1 = {
                    {1, 0, 0},
                    {1, 0, 1},
                    {0, 1, 1}

/*                    {1, 0, 0, 0, 1},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 1},
                    {0, 0, 1, 0, 1},*/

/*                    {1, 0, 0, 1, 1, 0, 1, 0, 1, 0},
                    {0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 0, 1, 0, 0, 1, 0},
                    {0, 0, 1, 0, 1, 1, 0, 0, 0, 0},
                    {0, 1, 1, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 1, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
                    {0, 0, 1, 0, 0, 1, 0, 0, 0, 0},
                    {1, 0, 1, 0, 0, 1, 0, 1, 1, 1},
                    {1, 0, 1, 0, 0, 1, 0, 0, 1, 1}*/};

            //inputs for Task 2 - The Shortest paths
            int[][] input2 = {
/*                    {1, 0, 0},
                    {1, 1, 0},
                    {1, 1, 0}*/

/*                    {1, 0, 0, 0, 0},
                    {0, 1, 1, 0, 0},
                    {0, 1, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0}*/

                    {1, 0, 1, 1, 1, 1, 0, 0, 0, 0},
                    {0, 1, 1, 1, 1, 1, 0, 0, 0, 0},
                    {0, 1, 1, 1, 0, 1, 0, 0, 0, 0},
                    {0, 0, 1, 0, 1, 1, 0, 1, 0, 0},
                    {0, 1, 1, 0, 0, 1, 0, 0, 0, 0},
                    {1, 0, 1, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
                    {0, 0, 1, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 1, 0, 0, 1, 0, 1, 1, 1},
                    {1, 0, 1, 0, 0, 1, 0, 0, 1, 1}};

            //inputs for Task 3 - Submarines
            int[][] input3 = {
                    //1 sub
/*                    {1, 1, 0, 1, 1},
                    {1, 0, 0, 1, 1},
                    {1, 0, 0, 1, 1}*/

/*
                    //1 sub
                    {1,0,0,1,1},
                    {1,0,0,1,1},
                    {0,1,0,1,1}
*/
                    //2 subs
/*                    {1, 0, 1, 1},
                    {1, 0, 1, 1},
                    {1, 0, 1, 1}*/

                    //3 subs
/*                    {1, 1, 0, 1, 1},
                    {0, 0, 0, 1, 1},
                    {1, 1, 0, 1, 1},
                    {1, 1, 0, 1, 1}*/

                    //3 subs
                    {1, 1, 0, 1, 1},
                    {0, 0, 0, 1, 1},
                    {1, 1, 0, 0, 0},
                    {1, 1, 0, 0, 0}
            };

            //input for Task 4 - The Lightest paths
            int[][] input4 = {
                    //(0,0) --> (2,2) = (0,0) (0,1) (1,2) (2,2)
                    //(2,0) --> (0,2) = (2,0) (2,1) (1,2) (0,2)
/*                    {100, 100, 100},
                    {500, 900, 300},
                    {400, 150, 200}*/

                    //(0,0) --> (2,1) = (0,0) (1,0) (2,1)
                    //(3,0) --> (1,2) = (3,0) (2,1) (1,2)
/*                    {25, 67, 55, 90},
                    {17, 42, 39, 48},
                    {15, 10, 5, 103},
                    {2, 59, 86, 74}*/

/*                    {100,100,100},
                    {100,100,100},
                    {100,100,100}*/

                    {100, 100, 100},
                    {100, 600, 100},
                    {100, 100, 100}
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

                        List<HashSet<Node<Index>>> allReachableNodes = new ArrayList<>((List<HashSet<Node<Index>>>) fromServer.readObject());
                        printMatrix(input1);

                        if (!allReachableNodes.isEmpty()) {
                            System.out.println("All Reachable Nodes:");
                            allReachableNodes.forEach(System.out::println);
                        } else
                            System.out.println("There are no reachable nodes in this Matrix.");
                    }
                    case "shortest path" -> {
                        toServer.writeObject("shortest path");
                        toServer.writeObject(input2);
                        Matrix matrix = new Matrix(input2);

                        Index source = createIndex(matrix, "Source");
                        Index destination = createIndex(matrix, "Destination");
                        toServer.writeObject(source);
                        toServer.writeObject(destination);

                        System.out.println("Current Matrix:");
                        System.out.println(matrix);

                        List<List<Index>> shortestPaths = new ArrayList<>((List<List<Index>>) fromServer.readObject());

                        if (!shortestPaths.isEmpty()) {
                            System.out.println("\nAll Shortest path from " + source + " to " + destination + ":");
                            shortestPaths.forEach(System.out::println);
                        } else //if there's no valid path from source to destination
                            System.out.println("There is no path from " + source + " to " + destination);
                    }
                    case "find submarines" -> {
                        toServer.writeObject("find submarines");
                        toServer.writeObject(input3);

                        System.out.println("Current Matrix:");
                        printMatrix(input3);

                        int submarinesCount = (int) fromServer.readObject();
                        System.out.println("Valid submarines count is: " + submarinesCount);
                    }
                    case "shortest path weighted graph" -> {
                        toServer.writeObject("shortest path weighted graph");
                        toServer.writeObject(input4);

                        Matrix matrix = new Matrix(input4);
                        Index source = createIndex(matrix, "Source");
                        Index destination = createIndex(matrix, "Destination");

                        System.out.println("\nCurrent Matrix:");
                        System.out.println(matrix);

                        toServer.writeObject(source);
                        toServer.writeObject(destination);
                        LinkedList<List<Index>> lightestPaths = new LinkedList<>((LinkedList<List<Index>>) fromServer.readObject());

                        if (!lightestPaths.isEmpty()) {
                            System.out.println("All Lightest paths from " + source + " to " + destination + ":");
                            lightestPaths.forEach(System.out::println);
                        } else
                            System.out.println("There is no path from " + source + " to " + destination);
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
