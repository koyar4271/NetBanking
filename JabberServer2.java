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
                    if(UserManager.registerUser(username, password))out.println("Registration successful.");
                    else {
                        System.out.println("the username is used.");
                        out.println("the username is used.");
                    }
                } else if (action.equals("LOGIN")) {
                    if (UserManager.loginUser(username, password)) {
                        out.println("LOGIN SUCCEED");

                        String select = in.readLine();
                            String otheruser = in.readLine();
                            String amount = in.readLine();
                            String actions = in.readLine();
                            System.out.println("Input select: " + select + "\notheruser: " + otheruser + "\namount: " + amount + "\naction: " + actions);
                            UserManager.actionUser(select, username, otheruser, amount, out);
                        
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

    public static synchronized boolean usedUser(String username) {
        for (int i = 0; i < userCount; i++) {
            if (userAccounts[i].getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean registerUser(String username, String password) {
        if(usedUser(username)) return false;
        userAccounts[userCount] = new UserAccount(username, password, 100000);
        userCount++;
        return true;
    }

    public static synchronized boolean loginUser(String username, String password) {
        for (int i = 0; i < userCount; i++) {
            if (userAccounts[i].getUsername().equals(username) && userAccounts[i].getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private static synchronized int showUserbalance(String user) {
        for (int i = 0; i < userCount; i++) {
            if (userAccounts[i].getUsername().equals(user)) {
                return userAccounts[i].getBalance();
            }
        }
        return -1; // Default value if user is not found
    }

    private static synchronized void depositUser(String user, int amount) {
        for (int i = 0; i < userCount; i++) {
            if (userAccounts[i].getUsername().equals(user)) {
                userAccounts[i].depositBalance(amount);
            }
        }
    }

    private static synchronized void withdrawalUser(String user, int amount) {
        for (int i = 0; i < userCount; i++) {
            if (userAccounts[i].getUsername().equals(user)) {
                userAccounts[i].withdrawalBalance(amount);
            }
        }
    }

    private static synchronized void transferUser(String me, String otheruser ,int amount) {
        withdrawalUser(me, amount);
        depositUser(otheruser, amount);
    }

    public static synchronized void actionUser(String select, String me, String otheruser ,String a, PrintWriter out) {
        int amount = 0;
        if(checkamount(a)){
            amount = Integer.parseInt(a);

            if (select.equals("1")){
                depositUser(me, amount);
                System.out.println(showUserbalance(me));
                out.println("deposit successful. your balance is " + showUserbalance(me));
    
            } else if (select.equals("2")){
                withdrawalUser(me, amount);
                System.out.println(showUserbalance(me));
                out.println("withdrawal successful. your balance is " + showUserbalance(me));
    
            } else if (select.equals("3")){
                transferUser(me, otheruser, amount);
                System.out.println(showUserbalance(me));
                System.out.println(showUserbalance(otheruser));
                out.println("transfer successful. your balance is " + showUserbalance(me)+
                                     " "+otheruser+" balance is " + showUserbalance(otheruser));        
    
            } else {
                System.out.print("Invalid select");
                out.println("Invalid select");
            }

        } else {
            System.out.print("Invalid amount");
            out.println("Invalid amount");
        }
        
    }

    public  static boolean checkamount(String amount){
        for(int i = 0; i < amount.length(); i++) {
            if(Character.isDigit(amount.charAt(i))) {
              continue;
            }else {
              return false;
            }
        }
        return true;
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

    public void depositBalance(int amount){
        this.balance += amount;
    }

    public void withdrawalBalance(int amount){
        this.balance -= amount;
    }
}

public class JabberServer2 {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
