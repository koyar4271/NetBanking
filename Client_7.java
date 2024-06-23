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

public class Client_7 {
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
        System.out.println(response);
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
    private Client client;

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

        initMainPanel();
    }

    private void initMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel label = new JLabel("Do you have your bank account?");
        label.setPreferredSize(new Dimension(200, 100));

        JButton yesButton = new JButton("Yes");
        yesButton.setPreferredSize(new Dimension(100, 50));
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

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(label, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
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

        JLabel l1 = new JLabel("Enter your username and password.", JLabel.CENTER);
        l1.setPreferredSize(new Dimension(250, 20));

        JLabel l2 = new JLabel("Your username:", JLabel.RIGHT);
        l2.setPreferredSize(new Dimension(100, 50));

        JLabel l3 = new JLabel("Your password:", JLabel.RIGHT);
        l3.setPreferredSize(new Dimension(100, 50));

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

        JLabel l1 = new JLabel("You can create your account.", JLabel.CENTER);
        l1.setPreferredSize(new Dimension(250, 20));

        JLabel l2 = new JLabel("Your username:", JLabel.RIGHT);
        l2.setPreferredSize(new Dimension(100, 50));

        JLabel l3 = new JLabel("Your password:", JLabel.RIGHT);
        l3.setPreferredSize(new Dimension(100, 50));

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

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(l1, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(l2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(depositButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(withdrawButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        panel.add(transferButton, gbc);

        getContentPane().add(panel, BorderLayout.CENTER);
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
        l1.setPreferredSize(new Dimension(250, 20));

        JTextField amountField = new JTextField(10);
        amountField.setPreferredSize(new Dimension(200, 20));

        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 50));
        submitButton.addActionListener(evt -> {
            String amount = amountField.getText();
            try {
                client.sendAmountSelect("DEPOSIT", null, amount, "DEPOSIT");
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
                client.sendAmountSelect("WITHDRAWAL", null, amount, "WITHDRAWAL");
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
                client.sendAmountSelect("TRANSFER", recipient, amount, "TRANSFER");
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
