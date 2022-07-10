package clientserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class listens to client requests over a socket using a TPC connection<br>
 * and uses the {@link IHandler} interface to execute the required task.
 */
public class Server {
    private final int port;
    //using 'volatile' keyword to ensure that updates to the variable propagate predictably to other threads.
    private volatile boolean activeServer;
    private ThreadPoolExecutor clientsPool;

    public Server(int port) throws IllegalArgumentException {
        if (!validatePort(port))
            throw new IllegalArgumentException("Invalid port");

        this.port = port;
        this.activeServer = true;

        System.out.println("Server is live.");
    }

    /**
     * A method to validate the port number.<br>
     * Valid ports are from 1024 to 49151 inclusive (Registered ports).
     *
     * @param port The port to validate.
     * @return true if port is valid, else false.
     */
    private boolean validatePort(int port) {
        return port >= 1024 && port <= 49151;
    }

    /**
     * This method listens for client incoming requests & executes them.
     *
     * @param clientHandler The {@link IHandler} Interface
     */
    public void handleClients(IHandler clientHandler) {

        new Thread(() -> {
            this.clientsPool = new ThreadPoolExecutor(10, 20, 500,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

            try {
                //Default IP is --> 127.0.0.1
                //backlog - max amount of clients that can try to connect to the server simultaneously
                ServerSocket serverSocket = new ServerSocket(this.port, 20);

                while (activeServer) {
                    //creating operational socket
                    Socket serverToSpecificClient = serverSocket.accept();

                    //This Runnable instance will be inserted to the clients pool
                    Runnable singleClientHandling = () -> {
                        try {
                            clientHandler.handleClient(serverToSpecificClient.getInputStream(),
                                    serverToSpecificClient.getOutputStream());

                            serverToSpecificClient.getOutputStream().close();
                            serverToSpecificClient.getInputStream().close();
                            serverToSpecificClient.close(); //closing connection to a specific client
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    };
                    //executes the given task sometime in the future
                    clientsPool.execute(singleClientHandling);
                }
                //Closing the server & stop listening for incoming connections from clients
                serverSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * This method responsible for closing the server.<br>
     * The method invokes the 'shutdown()' method of the {@link ThreadPoolExecutor} class.
     */
    private void closeServer() {
        if (this.activeServer) {
            this.activeServer = false;

            if (this.clientsPool != null)
                //Stop accepting tasks and finish current running tasks
                clientsPool.shutdown();
        }
        System.out.println("Server closed.");
    }

    public static void main(String[] args) {
        Server server = new Server(8010);
        server.handleClients(new MatrixHandler());

        Scanner in = new Scanner(System.in);

        System.out.println("Type 'stop' to shutdown the server.");

        while (!in.next().equalsIgnoreCase("stop")) {
            System.out.println("Invalid command, try again!");
        }
        server.closeServer();
    }
}
