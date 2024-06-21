import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class Server {
    private static final int PORT = 8086;
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
    private static final String DATABASE_NAME = "NetBanking";
    private static final String PROPATIES = "?characterEncoding=UTF-8&serverTimezone=UTC";
    private static final String DB_URL = "jdbc:mysql://localhost/" + DATABASE_NAME + PROPATIES;
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2784koya";

    public static void registerUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO users (username, password, balance) VALUES (?, ?, 100000)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean loginUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                return rs.next(); // true if user found, false otherwise
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
