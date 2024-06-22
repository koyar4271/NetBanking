import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

class Client {
    static final int PORT = 8089;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private UserInput userInput;

    public Client() {
        userInput = new UserInput();
    }

    public void connect(String serverAddress, int serverPort) throws IOException {
        InetAddress addr = InetAddress.getByName(serverAddress);
        socket = new Socket(addr, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        System.out.println("Connected to server.");
    }

    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Connection closed.");
        }
    }

    public void process() throws IOException {
        while (true) {
            System.out.println("Do you have your bank account? y/n");
            String answer = userInput.readLine();

            if (answer.equals("n")) {
                createAccount();
            } else if (answer.equals("y")) {
                login();
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
                out.println("Proccess");
                out.println("Finish");
                out.println("END");
                break;
            }

            String response = in.readLine();
            System.out.println(response);
            
            if(response.equals("LOGIN SUCCEED")){
                //System.out.println("check");
                select();
                String responses = in.readLine();
                System.out.println(responses);


            }
        }
        out.println("END");
    }

    private void createAccount() throws IOException {
        System.out.println("You can create your account.");
        String username = userInput.readUsername();
        String password = userInput.readPassword();
        out.println(username);
        out.println(password);
        out.println("REGISTER");
    }

    private void login() throws IOException {
        System.out.println("Input your username and password.");
        String username = userInput.readUsername();
        String password = userInput.readPassword();
        out.println(username);
        out.println(password);
        out.println("LOGIN");
    }

    private void select() throws IOException {
        String select;
        String otheruser;
        String amount;
        System.out.println("select the operation you want to perform.");
        System.out.println("1 : deposit");
        System.out.println("2 : withdrawal");
        System.out.println("3 : transfer");
        select = userInput.readSelect();
        if((select.equals("1")||select.equals("2")||select.equals("3"))){
            otheruser = userInput.readotheruser(select);
            //System.out.println("otheruser check1");
            amount = userInput.readamount(select);
            //System.out.println("amount check1");
            out.println(select);
            //System.out.println("select check");
            out.println(otheruser);
            //System.out.println("otheruser check");
            out.println(amount);
            //System.out.println("amount check");
            out.println("SELECT");
            System.out.println("send...");
        } else {
            out.println("0");
            out.println("error");
            out.println("0");
            out.println("SELECT");
            System.out.println("error send...");
        }

        
    
        

    }
}

class UserInput {
    private BufferedReader userInputReader;

    public UserInput() {
        userInputReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public String readLine() throws IOException {
        return userInputReader.readLine();
    }

    public String readUsername() throws IOException {
        System.out.print("Enter username: ");
        return readLine();
    }

    public String readPassword() throws IOException {
        System.out.print("Enter password: ");
        return readLine();
    }

    public String readSelect() throws IOException {
        System.out.print("Enter select number: ");
        return readLine();
    }

    public String readotheruser(String select) throws IOException {
        String user;
        if (select.equals("1")){
            user = "user";
        } else if (select.equals("2")){
            user = "server";
        } else if (select.equals("3")){
            System.out.print("Please enter another user of your transfer: ");
            user = readLine();
        } else {
            user = "invalid";
        }
        return user;
    }

    public String readamount(String select) throws IOException {
        String amount;
        String temp;
        if (select.equals("1")){
            while (true){
            System.out.print("Please enter the amount of your deposit: ");
            temp = readLine();
            if(checkamount(temp)){
                amount = temp;
                break;
            }
            else {
                System.out.println("invalid amount");
                continue;
            }
            }
        } else if (select.equals("2")){
            while (true){
            System.out.print("Please enter the amount of your withdrawal: ");
            temp = readLine();
            if(checkamount(temp)){
                amount = temp;
                break;
            }
            else {
                System.out.println("invalid amount");
                continue;
            }
            }
        } else if (select.equals("3")){
            while (true){
            System.out.print("Please enter the amount of your transfer: ");
            temp = readLine();
            if(checkamount(temp)){
                amount = temp;
                break;
            }
            else {
                System.out.println("invalid amount");
                continue;
            }
            }
        } else {
            String i = "Invalid select";
            System.out.println(i);
            amount = i;
        }
        System.out.println("readamount check");
        return amount;
    }
        

    public boolean checkamount(String amount){
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

public class Client_6 {
    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.connect("localhost", client.PORT);
            client.process();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
