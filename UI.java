import java.awt.BorderLayout;
//import javax.swing.JOptionPane;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

class UI extends JFrame{

    static UI frame;
    JButton b1, b2;

    public static void main(String args[]){
        frame = new UI("ネット銀行");
        frame.setVisible(true);
    }

    UI(String title){
    setTitle(title);
    setBounds(100, 100, 300, 250);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel p = new JPanel();

    JLabel l1 = new JLabel("アカウントをお持ちでしょうか?");
    l1.setPreferredSize(new Dimension(200, 100));

    b1 = new JButton("はい");
    b1.setPreferredSize(new Dimension(100, 50));
    b1.addActionListener(new login());

    b2 = new JButton("いいえ");
    b2.setPreferredSize(new Dimension(100, 50));
    b2.setOpaque(false);
    b2.addActionListener(new createaccount());

    p.add(l1);
    p.add(b1);
    p.add(b2);

    Container contentPane = getContentPane();
    contentPane.add(p, BorderLayout.CENTER);
  }

  //ログイン画面
  class login implements ActionListener{
    public void actionPerformed(ActionEvent e){
      JFrame frame = new JFrame("ログイン");
      frame.setVisible(true);
      frame.setBounds(100, 100, 300, 250);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      JPanel p = new JPanel();

      JLabel l1 = new JLabel("IDとパスワードを入力してください?");
      l1.setPreferredSize(new Dimension(200, 100));

      JButton b1 = new JButton("はい");
      b1.setPreferredSize(new Dimension(100, 50));

      JButton b2 = new JButton("いいえ");
      b2.setPreferredSize(new Dimension(100, 50));
      b2.setOpaque(false);

      p.add(l1);
      p.add(b1);
      p.add(b2);

      Container contentPane = frame.getContentPane();
      contentPane.add(p, BorderLayout.CENTER);
    }
  }

  //新規アカウント開設
  class createaccount implements ActionListener{
    public void actionPerformed(ActionEvent e){
      JFrame frame = new JFrame("アカウント新規開設");
      frame.setVisible(true);
      frame.setBounds(100, 100, 300, 250);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      JPanel p = new JPanel();

      JLabel msg = new JLabel("アカウントを新規開設できます");
      msg.setPreferredSize(new Dimension(200, 20));

      JLabel l1 = new JLabel("IDとパスワードを入力してください",JLabel.CENTER);
      l1.setPreferredSize(new Dimension(250, 20));

      JLabel l2 = new JLabel("ID:",JLabel.RIGHT);
      l2.setPreferredSize(new Dimension(100, 50));

      JLabel l3 = new JLabel("パスワード：",JLabel.RIGHT);
      l3.setPreferredSize(new Dimension(100, 50));
      

      JTextField id = new JTextField(10);
      id.setPreferredSize(new Dimension(200, 20));
      JPasswordField password = new JPasswordField(10);
      password.setPreferredSize(new Dimension(20, 20));


      p.add(msg);
      p.add(l1);
      p.add(l2);
      p.add(id);
      p.add(l3);
      p.add(password);


      Container contentPane = frame.getContentPane();
      contentPane.add(p, BorderLayout.CENTER);
    }
  }
}