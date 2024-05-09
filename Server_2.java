import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server_2 {
    public static final int PORT = 8080;
    private static final String USERDATA_FILE = "userdata.csv";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started: " + serverSocket);

        try {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted: " + socket);

                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    String username = in.readLine();
                    String password = in.readLine();
                    String action = in.readLine();

                    if (action.equals("REGISTER")) {
                        registerUser(username, password, out);
                    } else if (action.equals("LOGIN")) {
                        loginUser(username, password, out);
                    }
                } finally {
                    socket.close();
                }
            }
        } finally {
            serverSocket.close();
        }
    }

    private static void registerUser(String username, String password, PrintWriter out) throws IOException {
        FileWriter fileWriter = new FileWriter(USERDATA_FILE, true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(username + "," + password);
        printWriter.close();
        out.println("Account created successfully!");
    }

    private static void loginUser(String username, String password, PrintWriter out) throws IOException {
        // Implement login logic
    }
}
