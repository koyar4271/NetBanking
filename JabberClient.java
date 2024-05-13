import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

class Cstoc extends Thread{
    private boolean end = false;
    private BufferedReader in;
    String str;

    synchronized boolean end() {
        return end;
    }

    public void set(BufferedReader i){
        in=i;
    }

    public void run(){
        try{
            while (true) {
                
                try{
                    str = in.readLine();
                }catch(IOException e){
                    str = "nul";
                }
                if (str.equals("END"))break; 
                if (str == "nul") continue;
                System.out.println(str);
            }
        }finally{
            System.out.println("Server END");
            end = true;
        }
    }
}

class Cctos extends Thread{
    private boolean end = false;
    private PrintWriter out;
    Scanner scanner = new Scanner(System.in);

    synchronized boolean end() {
        return end;
    }

    public void set(PrintWriter o){
        out=o;
    }

    public void run(){
        try{
            while (true) {
                String input = scanner.nextLine();
                out.println(input);
                if(input.equals("END"))break;
                
            }
        }finally{
            System.out.println("Client END");
            end = true;
        }
    }
}

public class JabberClient {
    public static void main(String[] args)throws IOException {
    int PORT = Integer.parseInt(args[0]);
    InetAddress addr =InetAddress.getByName("localhost");
    System.out.println("addr = " + addr);
    Socket socket =new Socket(addr, PORT);
        System.out.println("socket = " + socket);
        BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out =new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        Cstoc servercomment = new Cstoc();
        Cctos clientcomment = new Cctos();
        servercomment.set(in);
        clientcomment.set(out);
        clientcomment.start();
        servercomment.start();
        while(true){
        if(servercomment.end()||clientcomment.end())break;
        }
        System.out.println("completely END...");
        socket.close();
        System.exit(0);
    }
}
