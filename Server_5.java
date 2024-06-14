import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class Server {
    private static final int PORT = 8081;
    private static ServerSocket serverSocket;
    private static int clientCount = 0;

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection established: " + clientSocket);
                incrementClientCount();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized static void incrementClientCount() {
        clientCount++;
        System.out.println("Now " + clientCount + " client connected.");
    }

    public synchronized static void decrementClientCount() {
        clientCount--;
        System.out.println("A client exited\nThe number of clients: " + clientCount);
    }

    public synchronized static int getClientCount() {
        return clientCount;
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true)
        ) {
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String username = in.readLine();
                String password = in.readLine();
                String action = in.readLine();

                System.out.println("Input username: " + username + "\nInput password: " + password + "\naction: " + action);
                if (action.equals("REGISTER")) {
                    UserManager.registerUser(username, password);
                    out.println("Registration successful.");
                } else if (action.equals("LOGIN")) {
                    if (UserManager.loginUser(username, password)) {
                        out.println("LOGIN SUCCEED");
                    } else {
                        out.println("LOGIN FAILED");
                        out.println("input: " + username);
                    }
                } else if (action.equals("END")) {
                    if (Server.getClientCount() == 1) {
                        System.out.println("All clients exited\nDo you want to continue? y/n");
                        if (userInputReader.readLine().equals("n")) {
                            System.out.println("Shutdown Server...");
                            System.exit(0);
                        }
                    }
                    Server.decrementClientCount();
                    break;
                } else {
                    out.println("Invalid action.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class UserManager {
    private static UserAccount[] userAccounts = new UserAccount[1000];
    private static int userCount = 0;

    public static synchronized void registerUser(String username, String password) {
        userAccounts[userCount] = new UserAccount(username, password, 100000);
        userCount++;
    }

    public static synchronized boolean loginUser(String username, String password) {
        for (int i = 0; i < userCount; i++) {
            if (userAccounts[i].getUsername().equals(username) && userAccounts[i].getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}

class UserAccount {
    private String username;
    private String password;
    private int balance;

    public UserAccount(String username, String password, int balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getBalance() {
        return balance;
    }
}

public class Server_5 {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
