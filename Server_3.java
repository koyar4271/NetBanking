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
    private static UserAccount[] userAccounts = new UserAccount[1000];
    private static int userCount = 0, clientCount = 0;
    private static ServerSocket serverSocket;

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection established: " + clientSocket);
                clientCount ++;
                System.out.println("Now "+clientCount+" client connected.");
                // クライアント接続を新しいスレッドで処理
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
                ) {
                while (true) {
                    String username = in.readLine();
                    String password = in.readLine();
                    String action = in.readLine();

                    System.out.println("Input userneme: " + username +
                            "\nInput password: " + password + "\naction: " + action);
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
                        if(clientCount == 1){
                            System.out.println("All clients exited\nDo you want to continue? y/n");
                            if(userInputReader.readLine().equals("n")){
                                System.out.println("Shutdown Server...");
                                System.exit(0);
                            }
                        }
                        clientCount --;
                        System.out.println("A client is exited\nThe number of clients: "+clientCount);
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

    private static synchronized void registerUser(String username, String password) {
        userAccounts[userCount] = new UserAccount(username, password, "100000");
        userCount++;
    }

    private static synchronized boolean loginUser(String username, String password) {
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

public class Server_3 {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}