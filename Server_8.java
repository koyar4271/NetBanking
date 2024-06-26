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
    private static final int PORT = 8098;
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
                System.out.println("start!");
                String username = in.readLine();
                String password = in.readLine();
                String action = in.readLine();

                System.out.println("Input username: " + username + "\nInput password: " + password + "\naction: " + action);

                if (action.equals("REGISTER")) {
                    if(UserManager.registerUser(username, password)) {
                        out.println("Registration successful.");
                    } else {
                        System.out.println("the username is used.");
                        out.println("the username is used.");
                    }
                } else if (action.equals("LOGIN")) {
                    if (UserManager.loginUser(username, password)) {
                        System.out.println("LOGIN SUCCEED");
                        out.println("LOGIN SUCCEED");
                        while (true) {
                            String select = in.readLine();
                            String otheruser = in.readLine();
                            String amount = in.readLine();
                            String actions = in.readLine();
                            System.out.println("Input select: " + select + "\notheruser: " + otheruser + "\namount: " + amount + "\naction: " + actions);
                            if (select.equals("LOGOUT")) {
                                System.out.println("end input from this user");
                                break;
                            }
                            UserManager.actionUser(select, username, otheruser, amount, out);
                        }
                    } else {
                        System.out.println("LOGIN FAILED");
                        System.out.println("cannot find" + username);
                        out.println("LOGIN FAILED");
                        System.out.println("send loginfailed");
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
                Server.decrementClientCount();
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
    private static final String DB_PASSWORD = "rootpass";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static synchronized boolean usedUser(String username) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                return rs.next(); // true if user found, false otherwise
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized boolean registerUser(String username, String password) {
        if (usedUser(username)) return false;
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO users (username, password, balance) VALUES (?, ?, 100000)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized boolean loginUser(String username, String password) {
        try (Connection conn = getConnection()) {
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

    private static synchronized int showUserbalance(String username) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT balance FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Default value if user is not found
    }

    private static synchronized void updateUserBalance(String username, int newBalance) {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE users SET balance = ? WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newBalance);
                stmt.setString(2, username);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void depositUser(String username, int amount) {
        int currentBalance = showUserbalance(username);
        if (currentBalance != -1) {
            updateUserBalance(username, currentBalance + amount);
        }
    }

    private static synchronized void withdrawalUser(String username, int amount) {
        int currentBalance = showUserbalance(username);
        if (currentBalance != -1 && currentBalance >= amount) {
            updateUserBalance(username, currentBalance - amount);
        }
    }

    private static synchronized void transferUser(String me, String otheruser, int amount) {
        int currentBalanceMe = showUserbalance(me);
        if (currentBalanceMe != -1 && currentBalanceMe >= amount && usedUser(otheruser)) {
            withdrawalUser(me, amount);
            depositUser(otheruser, amount);
        }
    }

    public static synchronized void actionUser(String select, String me, String otheruser, String amountStr, PrintWriter out) {
        int amount;
        if (checkamount(amountStr)) {
            amount = Integer.parseInt(amountStr);
            if ("0".equals(select)) {
                out.println("invalid select.");
            } else if ("DEPOSIT".equals(select)) {
                depositUser(me, amount);
                out.println("deposit successful. your balance is " + showUserbalance(me));
            } else if ("WITHDRAEAL".equals(select)) {
                withdrawalUser(me, amount);
                out.println("withdrawal successful. your balance is " + showUserbalance(me));
            } else if ("TRANSFER".equals(select)) {
                if (usedUser(otheruser)) {
                    transferUser(me, otheruser, amount);
                    out.println("transfer successful. your balance is " + showUserbalance(me) +
                            "\n" + otheruser + " balance is " + showUserbalance(otheruser));
                } else {
                    out.println(otheruser + " is not registered");
                }
            } else {
                out.println("Invalid select");
            }
        } else {
            out.println("Invalid amount");
        }
    }

    public static boolean checkamount(String amount) {
        for (int i = 0; i < amount.length(); i++) {
            if (!Character.isDigit(amount.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}

public class Server_8 {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
