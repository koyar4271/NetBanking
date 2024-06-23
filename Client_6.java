import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Client_6 {
    public static void main(String[] args) {
        UI frame = new UI("Net Banking");
        frame.setVisible(true);
    }
}

class Client {
    static final int PORT = 8094;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private UI ui;

    public void setUI(UI ui) {
        this.ui = ui;
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

    public void sendCredentials(String username, String password, String action) throws IOException {
        out.println(username);
        out.println(password);
        out.println(action);
        String response = in.readLine();
        System.out.println(response);
        
        if (response != null) {
            System.out.println(response);
            if (response.equals("LOGIN SUCCEED")) {
                ui.showSelectAction();
            }else{
                ui.showResponse(response);
            }
        } else {
            System.out.println("No response from server.");
        }
        
    }

    public void sendamountselect(String select, String otheruser, String amount, String sub) throws IOException {
        out.println(select);
        out.println(otheruser);
        out.println(amount);
        out.println(sub);

        String response = in.readLine();
        System.out.println(response);
        ui.showResponse(response);
    }

    public void logout() throws IOException {
        out.println("LOGOUT");
        String response = in.readLine();
        System.out.println(response);
        if (response != null) {
            ui.showResponse(response);
        }
        close();
    }
}

class UI extends JFrame {
    static UI frame;
    JButton b1, b2, b3;
    Client client;

    UI(String title) {
        setTitle(title);
        setBounds(100, 100, 600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client = new Client();
        client.setUI(this);

        try {
            client.connect("localhost", Client.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel l1 = new JLabel("Do you have your bank account?");
        l1.setPreferredSize(new Dimension(200, 100));

        b1 = new JButton("yes");
        b1.setPreferredSize(new Dimension(100, 50));
        b1.setFocusable(false);
        b1.addActionListener(new LoginAction());

        b2 = new JButton("no");
        b2.setPreferredSize(new Dimension(100, 50));
        b1.setFocusable(false);
        b2.addActionListener(new CreateAccountAction());

        b3 = new JButton("END");
        b3.setPreferredSize(new Dimension(100, 50));
        b1.setFocusable(false);
        b3.addActionListener(new ENDAction());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        p.add(l1, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        p.add(b1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        p.add(b2, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        p.add(b3, gbc);

        Container contentPane = getContentPane();
        contentPane.add(p, BorderLayout.CENTER);

        this.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                p.requestFocusInWindow();
            }
        });
    }

    public void showResponse(String response) {
        JFrame frame = new JFrame("Response");
        frame.setVisible(true);
        frame.setBounds(100, 100, 600, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel msg = new JLabel(response);
        msg.setPreferredSize(new Dimension(300, 50));
    
        JButton submitButton = new JButton("Return to Main Menu");
        submitButton.setPreferredSize(new Dimension(250, 50));
        submitButton.setFocusable(false);
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
    
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        p.add(msg, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        p.add(submitButton, gbc);
    
        Container contentPane = frame.getContentPane();
        contentPane.add(p, BorderLayout.CENTER);

        this.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                p.requestFocusInWindow();
            }
        });
    }

    class LoginAction implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("login");
            frame.setVisible(true);
            frame.setBounds(100, 100, 600, 500);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel p = new JPanel();
            p.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JLabel msg = new JLabel("login");
            msg.setPreferredSize(new Dimension(200, 20));

            JLabel l1 = new JLabel("enter your username and password.", JLabel.CENTER);
            l1.setPreferredSize(new Dimension(250, 20));

            JLabel l2 = new JLabel("your username:", JLabel.RIGHT);
            l2.setPreferredSize(new Dimension(100, 50));

            JLabel l3 = new JLabel("your password", JLabel.RIGHT);
            l3.setPreferredSize(new Dimension(100, 50));

            JTextField id = new JTextField(10);
            id.setPreferredSize(new Dimension(200, 20));
            JPasswordField password = new JPasswordField(10);
            password.setPreferredSize(new Dimension(200, 20));

            JButton submitButton = new JButton("Submit");
            submitButton.setPreferredSize(new Dimension(100, 50));
            submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String username = id.getText();
                    String pwd = new String(password.getPassword());
                    try {
                        client.sendCredentials(username, pwd, "LOGIN");
                        frame.dispose();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            p.add(msg);
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.insets = new Insets(10, 10, 10, 10);
            p.add(l1, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 1;
            p.add(l2, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            p.add(id, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            p.add(l3, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            p.add(password, gbc);

            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.gridwidth = 3;
            p.add(submitButton, gbc);

            Container contentPane = frame.getContentPane();
            contentPane.add(p, BorderLayout.CENTER);
        }
    }

    class CreateAccountAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("sign up");
            frame.setVisible(true);
            frame.setBounds(100, 100, 600, 500);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel p = new JPanel();
            p.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JLabel msg = new JLabel("sign up");
            msg.setPreferredSize(new Dimension(200, 20));

            JLabel l1 = new JLabel("You can create your account.", JLabel.CENTER);
            l1.setPreferredSize(new Dimension(250, 20));

            JLabel l2 = new JLabel("new username:", JLabel.RIGHT);
            l2.setPreferredSize(new Dimension(100, 50));

            JLabel l3 = new JLabel("new password", JLabel.RIGHT);
            l3.setPreferredSize(new Dimension(100, 50));

            JTextField id = new JTextField(10);
            id.setPreferredSize(new Dimension(200, 20));
            JPasswordField password = new JPasswordField(10);
            password.setPreferredSize(new Dimension(200, 20));

            JButton submitButton = new JButton("Submit");
            submitButton.setPreferredSize(new Dimension(100, 50));
            submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String username = id.getText();
                    String pwd = new String(password.getPassword());
                    try {
                        client.sendCredentials(username, pwd, "REGISTER");
                        frame.dispose();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            p.add(msg);
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.insets = new Insets(10, 10, 10, 10);
            p.add(l1, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 1;
            p.add(l2, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            p.add(id, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            p.add(l3, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            p.add(password, gbc);

            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.gridwidth = 3;
            p.add(submitButton, gbc);

            Container contentPane = frame.getContentPane();
            contentPane.add(p, BorderLayout.CENTER);
        }
    }

    class ENDAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                client.sendCredentials("process", "Finish", "END");
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            dispose();
            System.exit(0);
        }
    }

    class SelectAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Select Action");
            frame.setVisible(true);
            frame.setBounds(100, 100, 600, 500);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel p = new JPanel();
            p.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JLabel msg = new JLabel("Select");
            msg.setPreferredSize(new Dimension(200, 20));

            JLabel l1 = new JLabel("Select the operation you want to perform.");
            l1.setPreferredSize(new Dimension(300, 100));

            JButton b1 = new JButton("1 : deposit");
            b1.setPreferredSize(new Dimension(100, 50));
            b1.addActionListener(new DepositAction());

            JButton b2 = new JButton("2 : withdrawal");
            b2.setPreferredSize(new Dimension(100, 50));
            b2.addActionListener(new WithdrawalAction());

            JButton b3 = new JButton("3 : transfer");
            b3.setPreferredSize(new Dimension(100, 50));
            b3.addActionListener(new TransferAction());

            p.add(msg);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.insets = new Insets(10, 10, 10, 10);
            p.add(l1, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 1;
            p.add(b1, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            p.add(b2, gbc);

            gbc.gridx = 2;
            gbc.gridy = 1;
            p.add(b3, gbc);

            Container contentPane = frame.getContentPane();
            contentPane.add(p, BorderLayout.CENTER);
        }
    }

    //public void showSelectAction() {
    //    new SelectAction().actionPerformed(null);
    //}
    public void showSelectAction() {
        JFrame frame = new JFrame("Select Action");
        frame.setVisible(true);
        frame.setBounds(100, 100, 600, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
    
        JLabel msg = new JLabel("Select");
        msg.setPreferredSize(new Dimension(200, 20));
    
        JLabel l1 = new JLabel("Select the operation you want to perform.");
        l1.setPreferredSize(new Dimension(300, 100));
    
        JButton b1 = new JButton("1 : deposit");
        b1.setPreferredSize(new Dimension(100, 50));
        b1.addActionListener(new DepositAction());
    
        JButton b2 = new JButton("2 : withdrawal");
        b2.setPreferredSize(new Dimension(100, 50));
        b2.addActionListener(new WithdrawalAction());
    
        JButton b3 = new JButton("3 : transfer");
        b3.setPreferredSize(new Dimension(100, 50));
        b3.addActionListener(new TransferAction());
    
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 50));
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    try {
                        client.sendamountselect("Logout", "", "", "SELECT");
                        frame.dispose();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    client.logout();
                    frame.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    
        p.add(msg);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        p.add(l1, gbc);
    
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        p.add(b1, gbc);
    
        gbc.gridx = 1;
        gbc.gridy = 1;
        p.add(b2, gbc);
    
        gbc.gridx = 2;
        gbc.gridy = 1;
        p.add(b3, gbc);
    
        gbc.gridx = 1;
        gbc.gridy = 2;
        p.add(logoutButton, gbc);
    
        Container contentPane = frame.getContentPane();
        contentPane.add(p, BorderLayout.CENTER);
    }

    class DepositAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Deposit");
            frame.setVisible(true);
            frame.setBounds(100, 100,600, 500);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel p = new JPanel();
            p.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JLabel msg = new JLabel("Deposit");
            msg.setPreferredSize(new Dimension(200, 20));

            JLabel l1 = new JLabel("Enter the amount of your deposit.", JLabel.CENTER);
            l1.setPreferredSize(new Dimension(250, 20));

            JLabel l2 = new JLabel("Amount:", JLabel.RIGHT);
            l2.setPreferredSize(new Dimension(100, 50));

            JTextField amo = new JTextField(10);
            amo.setPreferredSize(new Dimension(200, 20));

            JButton submitButton = new JButton("Submit");
            submitButton.setPreferredSize(new Dimension(100, 50));
            submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String amount = amo.getText();
                    try {
                        client.sendamountselect("1", "user", amount, "SELECT");
                        frame.dispose();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });


            p.add(msg);
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.insets = new Insets(10, 10, 10, 10);
            p.add(l1, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 1;
            p.add(l2, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            p.add(amo, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.gridwidth = 3;
            p.add(submitButton, gbc);

            Container contentPane = frame.getContentPane();
            contentPane.add(p, BorderLayout.CENTER);
        }
    }

    class WithdrawalAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Withdrawal");
            frame.setVisible(true);
            frame.setBounds(100, 100, 600, 500);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel p = new JPanel();
            p.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JLabel msg = new JLabel("Withdrawal");
            msg.setPreferredSize(new Dimension(200, 20));

            JLabel l1 = new JLabel("Enter the amount of your withdrawal.", JLabel.CENTER);
            l1.setPreferredSize(new Dimension(250, 20));

            JLabel l2 = new JLabel("Amount:", JLabel.RIGHT);
            l2.setPreferredSize(new Dimension(100, 50));

            JTextField amo = new JTextField(10);
            amo.setPreferredSize(new Dimension(200, 20));

            JButton submitButton = new JButton("Submit");
            submitButton.setPreferredSize(new Dimension(100, 50));
            submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String amount = amo.getText();
                    try {
                        client.sendamountselect("2", "server", amount, "SELECT");
                        frame.dispose();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            p.add(msg);
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.insets = new Insets(10, 10, 10, 10);
            p.add(l1, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 1;
            p.add(l2, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            p.add(amo, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.gridwidth = 3;
            p.add(submitButton, gbc);

            Container contentPane = frame.getContentPane();
            contentPane.add(p, BorderLayout.CENTER);
        }
    }

    class TransferAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Transfer");
            frame.setVisible(true);
            frame.setBounds(100, 100, 600, 500);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel p = new JPanel();
            p.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JLabel msg = new JLabel("Transfer");
            msg.setPreferredSize(new Dimension(200, 20));

            JLabel l1 = new JLabel("Enter the destination and amount.", JLabel.CENTER);
            l1.setPreferredSize(new Dimension(250, 20));

            JLabel l2 = new JLabel("Destination user:", JLabel.CENTER);
            l2.setPreferredSize(new Dimension(250, 20));

            JLabel l3 = new JLabel("Amount:", JLabel.RIGHT);
            l3.setPreferredSize(new Dimension(100, 50));

            JTextField another = new JTextField(10);
            another.setPreferredSize(new Dimension(200, 20));

            JTextField amo = new JTextField(10);
            amo.setPreferredSize(new Dimension(200, 20));

            JButton submitButton = new JButton("Submit");
            submitButton.setPreferredSize(new Dimension(100, 50));
            submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String amount = amo.getText();
                    String otheruser = another.getText();
                    try {
                        client.sendamountselect("3", otheruser, amount, "SELECT");
                        frame.dispose();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            

            p.add(msg);
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.insets = new Insets(10, 10, 10, 10);
            p.add(l1, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 1;
            p.add(l2, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            p.add(another, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            p.add(l3, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            p.add(amo, gbc);

            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.gridwidth = 3;
            p.add(submitButton, gbc);

            Container contentPane = frame.getContentPane();
            contentPane.add(p, BorderLayout.CENTER);
        }
    }
}
