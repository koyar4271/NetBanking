import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int PORT = 8080; // ポート番号を設定する．
    public static void main(String[] args) throws IOException {
        String [][] useraccount =new String[1000][3];
        int count=0;
        ServerSocket s = new ServerSocket(PORT); // ソケットを作成する
        System.out.println("Started: " + s);
        try {
            Socket socket = s.accept(); // コネクション設定要求を待つ
            try {
                System.out.println( "Connection accepted: " + socket);
                BufferedReader in =  new BufferedReader( new InputStreamReader( socket.getInputStream())); // データ受信用バッファの設定
                PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( socket.getOutputStream())), true); // 送信バッファ設定
                while (true) {
                    boolean isLoginSuccess=false;
                    String username = in.readLine();
                    String password = in.readLine();
                    String action = in.readLine();

                    if(action.equals("REGISTER")){
                        useraccount[count][0]=username;
                        useraccount[count][1]=password;
                        useraccount[count][2]="100000";
                        String responce = "username:"+useraccount[count][0]+" password:"+useraccount[count][1]+" action:"+action;
                        System.out.println(responce);
                        out.println(responce);
                        count ++;
                    }else if(action.equals("LOGIN")){
                        int usernum=0;
                        for(int i=0;i<count;i++){
                            if(username.equals(useraccount[i][0])){
                                usernum=i;
                                System.out.println("Existed User:"+useraccount[i][0]);
                                break;
                            }
                        }
                        if(useraccount[usernum][1].equals(password)){
                            System.out.println("the password is correct");
                            out.println("LOGIN SUCCEED");
                            isLoginSuccess =true;
                        }else{
                            System.out.println("[Warning]the password is incorrect\n"
                                +"Correct pass:"+useraccount[usernum][1]+"\nInput pass:"+password);
                            out.println("LOGIN FAILD");
                            isLoginSuccess =false;
                        }
                        String responce = "username:"+username+" password:"+password+" action:"+action;
                        System.out.println(responce);
                    }else if(action.equals("END")){
                        break;
                    }else{
                        System.out.println("Unvalid value");
                        System.exit(1);
                    }

                }
            } finally {
                System.out.println("closing...");
                socket.close();
            }
        } finally {
            s.close();
        }
    }
}