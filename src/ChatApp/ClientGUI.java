package ChatApp;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

public class ClientGUI {
    private JList<String> userList;
    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton sendImageButton;
    private JButton sendVoiceButton;

    public ClientGUI() {
        FlatDarculaLaf.setup();
        JFrame frame = new JFrame("Hi ChatApp.User");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        URL iconURL = getClass().getResource("/Resources/Icon.png");
        ImageIcon icon = new ImageIcon(iconURL);
        frame.setIconImage(icon.getImage());
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu menu = new JMenu("Settings");
        menuBar.add(menu);

        JMenuItem menuItem = new JMenuItem("Change Theme");
        menuItem.addActionListener(e -> {
            if (UIManager.getLookAndFeel() instanceof FlatDarculaLaf) {
                try {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                    unsupportedLookAndFeelException.printStackTrace();
                }
            } else {
                try {
                    UIManager.setLookAndFeel(new FlatDarculaLaf());
                } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                    unsupportedLookAndFeelException.printStackTrace();
                }
            }
            SwingUtilities.updateComponentTreeUI(frame);
        });
        menu.add(menuItem);

        userList = new JList<>();
        userList.addListSelectionListener(new UserSelectionListener());
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(150, 0));
        frame.add(userScrollPane, BorderLayout.WEST);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        frame.add(messageScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        messageField = new JTextField(15);
        messageField.addActionListener(e -> sendMessage());
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            String message = messageField.getText();
            if (selectedUser != null && !message.isEmpty()) {
                ChatClient.sendMessage(selectedUser, message);
                messageField.setText("");
            }
        });

        sendImageButton = new JButton("Send Image");
        sendImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                    String encodedString = Base64.getEncoder().encodeToString(fileContent);
                    ChatClient.sendMessage(userList.getSelectedValue(), encodedString+"^image");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        sendVoiceButton = new JButton("Send Voice");
        sendVoiceButton.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                ChatClient.sendVoiceMessage(selectedUser);
            }
        });
        bottomPanel.add(sendVoiceButton);
        bottomPanel.add(sendImageButton);
        bottomPanel.add(messageField);
        bottomPanel.add(sendButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void sendMessage() {
        String selectedUser = userList.getSelectedValue();
        String message = messageField.getText();
        if (selectedUser != null && !message.isEmpty()) {
            ChatClient.sendMessage(selectedUser, message);
            messageArea.append("\n" + "You: " + message);
            messageField.setText("");
        }
    }

    class UserSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                String selectedUser = userList.getSelectedValue();
                List<String> messages = ChatClient.getUserMessages(selectedUser);
                if (messages != null) {
                    messageArea.setText(String.join("\n", messages));
                } else {
                    messageArea.setText("No messages for this user.");
                }
            }
        }
    }

    public void updateUserList(List<String> users) {
        userList.setListData(users.toArray(new String[0]));
    }

    public void updateMessageArea(String messages) {
        messageArea.setText(messages);
    }
}