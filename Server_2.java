import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class Server {
    private static final int PORT = 8080;
    private ServerSocket serverSocket;
    private UserAccount[] userAccounts;
    private int userCount;

    public Server() {
        userAccounts = new UserAccount[1000];
        userCount = 0;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New connection established: " + socket);

                // Handle client connection in a new thread
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void registerUser(String username, String password) {
        userAccounts[userCount] = new UserAccount(username, password, "100000");
        userCount++;
    }

    private synchronized UserAccount findUser(String username) {
        for (int i = 0; i < userCount; i++) {
            if (userAccounts[i].getUsername().equals(username)) {
                return userAccounts[i];
            }
        }
        return null;
    }

    private synchronized boolean loginUser(String username, String password) {
        UserAccount user = findUser(username);
        return user != null && user.getPassword().equals(password);
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                while (true) {
                    String username = in.readLine();
                    String password = in.readLine();
                    String action = in.readLine();

                    System.out.println("Input userneme: "+username+
                    "\nInput password: "+password+"\naction: "+action);
                    if (action.equals("REGISTER")) {
                        registerUser(username, password);
                        out.println("Registration successful.");
                    } else if (action.equals("LOGIN")) {
                        if (loginUser(username, password)) {
                            out.println("LOGIN SUCCEED");
                        } else {
                            out.println("LOGIN FAILED");
                        }
                    } else if (action.equals("END")) {
                        System.out.println("end the while");;
                        break;
                    } else {
                        out.println("Invalid action.");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class UserAccount {
    private String username;
    private String password;
    private String balance;

    public UserAccount(String username, String password, String balance) {
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

    public String getBalance() {
        return balance;
    }
}

public class Server_2 {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
