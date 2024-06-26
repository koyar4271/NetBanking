import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

class Server {
    private static final int PORT = 8098;
    private static ServerSocket serverSocket;
    private static int clientCount = 0;
    private static DefaultTableModel tableModel;
    private static String dbPassword;

    public void start() {
        initializeGUI();
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

    public static void setDbPassword(String password) {
        dbPassword = password;
        UserManager.setDbPassword(password);
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

    private void initializeGUI() {
        JFrame frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        tableModel = new DefaultTableModel(new String[]{"Username", "Balance"}, 0);
        JTable table = new JTable(tableModel);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setVisible(true);

        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClientInfo();
            }
        });
        timer.start();
    }

    private static void displayMessage(String message) {
        System.out.println(message);
    }

    private void updateClientInfo() {
        synchronized (UserManager.class) {
            String[] users = UserManager.getAllUsers();
            tableModel.setRowCount(0); // Clear existing rows
            for (String username : users) {
                int balance = UserManager.showUserbalance(username);
                tableModel.addRow(new Object[]{username, balance});
            }
        }
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
                        boolean loop;
                        do {
                        String select = in.readLine();
                        String otheruser = in.readLine();
                        String amount = in.readLine();
                        String actions = in.readLine();
                        System.out.println("Input select: " + select + "\nOther user: " + otheruser + "\nAmount: " + amount + "\nAction: " + actions);
                        loop = UserManager.actionUser(select, username, otheruser, amount, out);
                        } while(loop);
                        
                    } else {
                        System.out.println("LOGIN FAILED");
                        System.out.println("cannot find" + username);
                        out.println("LOGIN FAILED");
                        System.out.println("send loginfailed");
                    }
                } else if (action.equals("END")) {
                    if (Server.getClientCount() == 1) {
                        // System.out.println("All clients exited\nDo you want to continue? y/n");
                        // if (userInputReader.readLine().equals("n")) {
                        //     System.out.println("Shutdown Server...");
                        //     System.exit(0);
                        // }
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
                //Server.decrementClientCount(); //?
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
    private static String dbPassword;

    public static void setDbPassword(String password) {
        dbPassword = password;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, dbPassword);
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

    public static synchronized int showUserbalance(String username) {
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

    public static synchronized boolean actionUser(String select, String me, String otheruser ,String a, PrintWriter out) {
        int amount = 0;
        if(checkamount(a)){
            amount = Integer.parseInt(a);
            if (select.equals("0")){
                System.out.println("LOGOUT");
                out.println("LOGOUT");
                return false;
    
            } else if (select.equals("1")){
                depositUser(me, amount);
                System.out.println(showUserbalance(me));
                out.println("Deposit successful. Your balance is " + showUserbalance(me));
                return true;
    
            } else if (select.equals("2")){
                withdrawalUser(me, amount);
                System.out.println(showUserbalance(me));
                out.println("Withdrawal successful. Your balance is " + showUserbalance(me));
                return true;
    
            } else if (select.equals("3")){
                if(usedUser(otheruser)){
                    transferUser(me, otheruser, amount);
                    System.out.println(showUserbalance(me));
                    System.out.println(showUserbalance(otheruser));
                    out.println("Transfer successful. Your balance is " + showUserbalance(me));        
                } else {
                    System.out.println(otheruser + " is not registered");
                    out.println(otheruser + " is not registered");
                }
                return true;
    
            } else {
                System.out.println("Invalid select");
                out.println("Invalid select");
                return false;
            }

        } else {
            System.out.println("Invalid amount");
            out.println("Invalid amount");
            return true;
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

    public static synchronized String[] getAllUsers() {
        try (Connection conn = getConnection()) {
            String sql = "SELECT username FROM users";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                
                // Create a list to store the usernames
                List<String> usernames = new ArrayList<>();
                while (rs.next()) {
                    usernames.add(rs.getString("username"));
                }
                
                // Convert the list to an array
                String[] usernameArray = new String[usernames.size()];
                return usernames.toArray(usernameArray);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0]; // Return an empty array in case of an error
        }
    }

}

public class Server_10 {
    public static void main(String[] args) {
        
        Server server = new Server();
        try (BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));){
            System.out.println("input MySQL password : ");
            String password = userInputReader.readLine();
            Server.setDbPassword(password);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}