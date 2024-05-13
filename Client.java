import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        InetAddress addr = InetAddress.getByName("localhost"); // IP アドレスへの変換 
        System.out.println("addr = " + addr);
        Socket socket = new Socket(addr, Server.PORT); // ソケットの生成 
        try {
            System.out.println("socket = " + socket);
            BufferedReader in =  new BufferedReader( new InputStreamReader( socket.getInputStream())); // データ受信用バッファの設定
            PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( socket.getOutputStream())), true); // 送信バッファ設定
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                System.out.println("Do you have your bank account? y/n");
                String answer = userInput.readLine();

                if (answer.equals("n")) {
                    System.out.println("You can create your account.");
                    System.out.println("Enter username: ");
                    String username = userInput.readLine();
                    System.out.println("Enter password: ");
                    String password = userInput.readLine();
                    out.println(username);
                    out.println(password);
                    out.println("REGISTER");

                } else if (answer.equals("y")) {
                    System.out.println("Enter username: ");
                    String username = userInput.readLine();
                    System.out.println("Enter password: ");
                    String password = userInput.readLine();
                    out.println(username);
                    out.println(password);
                    out.println("LOGIN"); // 登録したユーザー名とパスワードをログインするために使用するアクションを送信する
                    
                } else {
                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
                    out.println("Proccess");
                    out.println("Finish");
                    out.println("END");
                    break;
                }
                String responce=in.readLine();
                System.out.println(responce);
            }
            out.println("END");
        } finally {
            System.out.println("closing...");
            socket.close();
            }
    }
}