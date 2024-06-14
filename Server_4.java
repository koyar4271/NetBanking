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
                            String select = in.readLine();
                            String otheruser = in.readLine();
                            int amount = Integer.parseInt(in.readLine());
                            String actions = in.readLine();
                            System.out.println("Input select: " + select + "\notheruser: " + otheruser 
                            + "\namount: " + amount + "\naction: " + actions);
                            actionUser(select, username, otheruser, amount,out);
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
        userAccounts[userCount] = new UserAccount(username, password, 100000);
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

    private static synchronized void actionUser(String select, String me, String otheruser ,int amount, PrintWriter out) {
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

public class Server_4 {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}