package ChatApp;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoginGUI {
    private JTextField serverIPField;
    private JTextField passwordField;
    private JTextField usernameField;

    public LoginGUI() {
        FlatDarculaLaf.setup();
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        URL iconURL = getClass().getResource("/Resources/Icon.png");
        ImageIcon icon = new ImageIcon(iconURL);
        frame.setIconImage(icon.getImage());
        JPanel panel = new JPanel(new GridLayout(4, 2));

        JLabel serverIPLabel = new JLabel("Server IP:");
        serverIPField = new JTextField();
        panel.add(serverIPLabel);
        panel.add(serverIPField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JTextField();
        panel.add(passwordLabel);
        panel.add(passwordField);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        panel.add(usernameLabel);
        panel.add(usernameField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String serverIP = serverIPField.getText();
            String password = passwordField.getText();
            String username = usernameField.getText();
            if (!serverIP.isEmpty() && !password.isEmpty() && !username.isEmpty()) {
                frame.dispose();
                ChatClient.start(serverIP, password, username);
            }
        });
        panel.add(loginButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}