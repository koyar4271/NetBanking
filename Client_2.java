import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class Client_2 {
    private static final int PORT = 8080;
    private static final String SERVER_ADDRESS = "localhost";

    public static void main(String[] args) throws IOException {
        InetAddress serverAddr = InetAddress.getByName(SERVER_ADDRESS);
        System.out.println("Server address: " + serverAddr);

        try (Socket socket = new Socket(serverAddr, PORT);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {

            System.out.println("Connected to server: " + socket);

            while (true) {
                System.out.println("Do you have an account? (y/n)");
                String answer = userInput.readLine();

                if (answer.equalsIgnoreCase("n")) {
                    registerUser(userInput, out);
                } else if (answer.equalsIgnoreCase("y")) {
                    loginUser(userInput, out);
                } else {
                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
                    break;
                }

                String response = in.readLine();
                System.out.println("Server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void registerUser(BufferedReader userInput, PrintWriter out) throws IOException {
        System.out.println("Enter username: ");
        String username = userInput.readLine();
        System.out.println("Enter password: ");
        String password = userInput.readLine();
        out.println(username);
        out.println(password);
        out.println("REGISTER");
    }

    private static void loginUser(BufferedReader userInput, PrintWriter out) throws IOException {
        System.out.println("Enter username: ");
        String username = userInput.readLine();
        System.out.println("Enter password: ");
        String password = userInput.readLine();
        out.println(username);
        out.println(password);
        out.println("LOGIN");
    }
}
