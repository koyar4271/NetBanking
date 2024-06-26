import java.awt.BorderLayout;
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
import javax.swing.SwingConstants;

public class Client_9 {
    public static void main(String[] args) {
        UI frame = new UI("Net Banking");
        frame.setVisible(true);
    }
}

class Client {
    static final int PORT = 8098;
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
        if (response != null) {
            System.out.println(response);
            if (response.equals("LOGIN SUCCEED")) {
                ui.showSelectAction();
            } else {
                ui.showResponse(response);
            }
        } else {
            System.out.println("No response from server.");
        }
    }

    public void sendAmountSelect(String select, String otherUser, String amount, String sub) throws IOException {
        out.println(select);
        out.println(otherUser);
        out.println(amount);
        out.println(sub);
        String response = in.readLine();
        System.out.println(response);
        if (sub.equals("LOGOUT")) {

        } else {
            ui.showSelectResponse(response);
        }

        // ui.showSelectAction();
    }
}

class UI extends JFrame {
    private Client client;

    UI(String title) {
        setTitle(title);
        setBounds(100, 100, 800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client = new Client();
        client.setUI(this);

        try {
            client.connect("localhost", Client.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initMainPanel();
    }

    public void initMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel label = new JLabel("Do you have your bank account?");
        label.setPreferredSize(new Dimension(200, 100));
        label.setHorizontalAlignment(SwingConstants.CENTER);    //add
        label.setVerticalAlignment(SwingConstants.CENTER);  //add

        JButton yesButton = new JButton("Yes");
        yesButton.setPreferredSize(new Dimension(200, 100));
        yesButton.setFocusable(false);
        yesButton.addActionListener(new LoginAction(client));

        JButton noButton = new JButton("No");
        noButton.setPreferredSize(new Dimension(100, 50));
        noButton.setFocusable(false);
        noButton.addActionListener(new CreateAccountAction(client));

        JButton endButton = new JButton("END");
        endButton.setPreferredSize(new Dimension(100, 50));
        endButton.setFocusable(false);
        endButton.addActionListener(new ENDAction(client, this));

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        //gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER; //add
        gbc.fill = GridBagConstraints.HORIZONTAL; //add

        panel.add(label, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER; //add
        panel.add(yesButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(noButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(endButton, gbc);

        getContentPane().add(panel, BorderLayout.CENTER);
    }

    public void showResponse(String response) {
        new ResponseFrame(response);
    }

    public void showSelectResponse(String response) {
        new SelectResponseFrame(response, this);
    }

    public void showSelectAction() {
        new SelectAction(client, this).actionPerformed(null);
    }
}

class ResponseFrame extends JFrame {
    public ResponseFrame(String response) {
        setTitle("Response");
        setVisible(true);
        setBounds(100, 100, 600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel msg = new JLabel(response);
        msg.setPreferredSize(new Dimension(300, 50));

        JButton submitButton = new JButton("Return to Main Menu");
        submitButton.setPreferredSize(new Dimension(250, 50));
        submitButton.setFocusable(false);
        submitButton.addActionListener(e -> dispose());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(msg, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(submitButton, gbc);

        getContentPane().add(panel, BorderLayout.CENTER);
    }
}

class SelectResponseFrame extends JFrame {
    private UI ui;

    public SelectResponseFrame(String response, UI ui) {
        this.ui = ui;
        setTitle("Response");
        setVisible(true);
        setBounds(100, 100, 600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel msg = new JLabel(response);
        msg.setPreferredSize(new Dimension(500, 50));
        msg.setHorizontalAlignment(SwingConstants.CENTER);    //add
        msg.setVerticalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Return to Select Menu");
        submitButton.setPreferredSize(new Dimension(250, 50));
        submitButton.setFocusable(false);
        submitButton.addActionListener(e -> {
            dispose();
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        //gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(msg, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        getContentPane().add(panel, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                ui.showSelectAction();
            }
        });
    }
}

class LoginAction implements ActionListener {
    private Client client;

    public LoginAction(Client client) {
        this.client = client;
    }

    public void actionPerformed(ActionEvent e) {
        new LoginFrame(client);
    }
}

class LoginFrame extends JFrame {
    public LoginFrame(Client client) {
        setTitle("Login");
        setVisible(true);
        setBounds(100, 100, 600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel msg = new JLabel("Login");
        msg.setPreferredSize(new Dimension(200, 20));
        msg.setHorizontalAlignment(SwingConstants.CENTER);    //add
        msg.setVerticalAlignment(SwingConstants.CENTER);

        JLabel l1 = new JLabel("Enter your username and password.", JLabel.CENTER);
        l1.setPreferredSize(new Dimension(500, 20));
        l1.setHorizontalAlignment(SwingConstants.CENTER);    //add
        l1.setVerticalAlignment(SwingConstants.CENTER);

        JLabel l2 = new JLabel("Your username:", JLabel.RIGHT);
        l2.setPreferredSize(new Dimension(200, 50));

        JLabel l3 = new JLabel("Your password:", JLabel.RIGHT);
        l3.setPreferredSize(new Dimension(200, 50));

        JTextField id = new JTextField(10);
        id.setPreferredSize(new Dimension(200, 20));

        JPasswordField password = new JPasswordField(10);
        password.setPreferredSize(new Dimension(200, 20));

        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 50));
        submitButton.addActionListener(evt -> {
            String username = id.getText();
            String pwd = new String(password.getPassword());
            try {
                client.sendCredentials(username, pwd, "LOGIN");
                dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(msg, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(l1, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(l2, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(id, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(l3, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(password, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(submitButton, gbc);

        getContentPane().add(panel, BorderLayout.CENTER);
    }
}

class CreateAccountAction implements ActionListener {
    private Client client;

    public CreateAccountAction(Client client) {
        this.client = client;
    }

    public void actionPerformed(ActionEvent e) {
        new CreateAccountFrame(client);
    }
}

class CreateAccountFrame extends JFrame {
    public CreateAccountFrame(Client client) {
        setTitle("Sign Up");
        setVisible(true);
        setBounds(100, 100, 600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel msg = new JLabel("Sign Up");
        msg.setPreferredSize(new Dimension(200, 20));
        msg.setHorizontalAlignment(SwingConstants.CENTER);    //add
        msg.setVerticalAlignment(SwingConstants.CENTER);  

        JLabel l1 = new JLabel("You can create your account.", JLabel.CENTER);
        l1.setPreferredSize(new Dimension(250, 20));

        JLabel l2 = new JLabel("Your username:", JLabel.RIGHT);
        l2.setPreferredSize(new Dimension(150, 50));

        JLabel l3 = new JLabel("Your password:", JLabel.RIGHT);
        l3.setPreferredSize(new Dimension(150, 50));

        JTextField id = new JTextField(10);
        id.setPreferredSize(new Dimension(200, 20));
        JPasswordField password = new JPasswordField(10);
        password.setPreferredSize(new Dimension(200, 20));

        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 50));
        submitButton.addActionListener(evt -> {
            String username = id.getText();
            String pwd = new String(password.getPassword());
            try {
                client.sendCredentials(username, pwd, "REGISTER");
                dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(l1, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(l2, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(id, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(l3, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(password, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        panel.add(submitButton, gbc);

        getContentPane().add(panel, BorderLayout.CENTER);
    }
}

class ENDAction implements ActionListener {
    private Client client;
    private UI ui;

    public ENDAction(Client client, UI ui) {
        this.client = client;
        this.ui = ui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            client.sendCredentials(null, null, "END");
            ui.dispose();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }
}

class SelectAction implements ActionListener {
    private Client client;
    private UI ui;

    public SelectAction(Client client, UI ui) {
        this.client = client;
        this.ui = ui;
    }

    public void actionPerformed(ActionEvent e) {
        new SelectFrame(client, ui);
    }
}

class SelectFrame extends JFrame {
    public SelectFrame(Client client, UI ui) {
        setTitle("Operation Selection");
        setVisible(true);
        setBounds(100, 100, 600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel msg = new JLabel("Operation Selection");
        msg.setPreferredSize(new Dimension(300, 50));

        JLabel l1 = new JLabel("Enter your choice of banking operation.", JLabel.CENTER);
        l1.setPreferredSize(new Dimension(250, 20));

        JLabel l2 = new JLabel("Select an operation:", JLabel.CENTER);
        l2.setPreferredSize(new Dimension(350, 20));

        JButton depositButton = new JButton("Deposit");
        depositButton.setPreferredSize(new Dimension(150, 50));
        depositButton.addActionListener(evt -> {
            new DepositAction(client, ui).actionPerformed(null);
            dispose();
        });

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setPreferredSize(new Dimension(150, 50));
        withdrawButton.addActionListener(evt -> {
            new WithdrawalAction(client, ui).actionPerformed(null);
            dispose();
        });

        JButton transferButton = new JButton("Transfer");
        transferButton.setPreferredSize(new Dimension(150, 50));
        transferButton.addActionListener(evt -> {
            new TransferAction(client, ui).actionPerformed(null);
            dispose();
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(150, 50));
        logoutButton.addActionListener(evt -> {
            dispose();
            new LogoutAction(client, ui).actionPerformed(null);
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(msg, gbc);

        gbc.gridy = 1;
        panel.add(l1, gbc);

        gbc.gridy = 2;
        panel.add(l2, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(depositButton, gbc);

        gbc.gridx = 1;
        panel.add(withdrawButton, gbc);

        gbc.gridx = 2;
        panel.add(transferButton, gbc);

        gbc.gridx = 3;
        panel.add(logoutButton, gbc);

        getContentPane().add(panel, BorderLayout.CENTER);
        this.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                panel.requestFocusInWindow();
            }
        });
    }
}

class DepositAction implements ActionListener {
    private Client client;
    private UI ui;

    public DepositAction(Client client, UI ui) {
        this.client = client;
        this.ui = ui;
    }

    public void actionPerformed(ActionEvent e) {
        new DepositFrame(client, ui);
    }
}

class DepositFrame extends JFrame {
    public DepositFrame(Client client, UI ui) {
        setTitle("Deposit");
        setVisible(true);
        setBounds(100, 100, 600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel msg = new JLabel("Deposit");
        msg.setPreferredSize(new Dimension(300, 50));

        JLabel l1 = new JLabel("Enter deposit amount.", JLabel.CENTER);
        l1.setPreferredSize(new Dimension(300, 20));

        JTextField amountField = new JTextField(10);
        amountField.setPreferredSize(new Dimension(300, 20));

        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(300, 50));
        submitButton.addActionListener(evt -> {
            String amount = amountField.getText();
            try {
                client.sendAmountSelect("1", null, amount, "DEPOSIT");
                dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(msg, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        //gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(l1, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(amountField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(submitButton, gbc);

        getContentPane().add(panel, BorderLayout.CENTER);

    }
}

class WithdrawalAction implements ActionListener {
    private Client client;
    private UI ui;

    public WithdrawalAction(Client client, UI ui) {
        this.client = client;
        this.ui = ui;
    }

    public void actionPerformed(ActionEvent e) {
        new WithdrawalFrame(client, ui);
    }
}

class WithdrawalFrame extends JFrame {
    public WithdrawalFrame(Client client, UI ui) {
        setTitle("Withdrawal");
        setVisible(true);
        setBounds(100, 100, 600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel msg = new JLabel("Withdrawal");
        msg.setPreferredSize(new Dimension(300, 50));

        JLabel l1 = new JLabel("Enter withdrawal amount.", JLabel.CENTER);
        l1.setPreferredSize(new Dimension(250, 20));

        JTextField amountField = new JTextField(10);
        amountField.setPreferredSize(new Dimension(200, 20));

        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 50));
        submitButton.addActionListener(evt -> {
            String amount = amountField.getText();
            try {
                client.sendAmountSelect("2", null, amount, "WITHDRAWAL");
                dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(l1, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        panel.add(submitButton, gbc);

        getContentPane().add(panel, BorderLayout.CENTER);
    }
}

class TransferAction implements ActionListener {
    private Client client;
    private UI ui;

    public TransferAction(Client client, UI ui) {
        this.client = client;
        this.ui = ui;
    }

    public void actionPerformed(ActionEvent e) {
        new TransferFrame(client, ui);
    }
}

class TransferFrame extends JFrame {
    public TransferFrame(Client client, UI ui) {
        setTitle("Transfer");
        setVisible(true);
        setBounds(100, 100, 600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel msg = new JLabel("Transfer");
        msg.setPreferredSize(new Dimension(300, 50));

        JLabel l1 = new JLabel("Enter transfer details.", JLabel.CENTER);
        l1.setPreferredSize(new Dimension(250, 20));

        JLabel l2 = new JLabel("Recipient username:", JLabel.RIGHT);
        l2.setPreferredSize(new Dimension(150, 20));

        JLabel l3 = new JLabel("Amount:", JLabel.RIGHT);
        l3.setPreferredSize(new Dimension(150, 20));

        JTextField recipientField = new JTextField(10);
        recipientField.setPreferredSize(new Dimension(200, 20));

        JTextField amountField = new JTextField(10);
        amountField.setPreferredSize(new Dimension(200, 20));

        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 50));
        submitButton.addActionListener(evt -> {
            String recipient = recipientField.getText();
            String amount = amountField.getText();
            try {
                client.sendAmountSelect("3", recipient, amount, "TRANSFER");
                dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(l1, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(l2, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(recipientField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(l3, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(amountField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        panel.add(submitButton, gbc);

        getContentPane().add(panel, BorderLayout.CENTER);
    }
}

class LogoutAction implements ActionListener {
    private Client client;
    private UI ui;

    public LogoutAction(Client client, UI ui) {
        this.client = client;
        this.ui = ui;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            client.sendAmountSelect("0", null, "0", "LOGOUT");
            // UI frame = new UI("Net Banking");
            // frame.setVisible(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}