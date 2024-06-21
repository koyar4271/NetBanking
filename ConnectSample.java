import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectSample {

    public static void main(String[] args) {
        // DB接続用定数
        String DATABASE_NAME = "NetBanking";
        String PROPATIES = "?characterEncoding=UTF-8&serverTimezone=UTC";
        String URL = "jdbc:mysql://localhost/" + DATABASE_NAME + PROPATIES;
        // DB接続用・ユーザ定数
        String USER = "root"; // データベースに接続するユーザ名を指定
        String PASS = "2784koya"; // データベースに接続するパスワードを指定

        Connection conn = null;

        try {
            // MySQLに接続するためにドライバを読み込む
            Class.forName("com.mysql.cj.jdbc.Driver");

            // データベースに接続
            conn = DriverManager.getConnection(URL, USER, PASS);

            // 接続成功時の処理
            if (conn != null) {
                System.out.println("データベースに接続に成功しました");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 接続を閉じる
            try {
                if (conn != null) {
                    conn.close();
                    System.out.println("データベース接続をクローズしました");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
