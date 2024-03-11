import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ClientGUI {
    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendButton;

    public ClientGUI() {
        JFrame frame = new JFrame("Hi User");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        frame.add(messagePanel, BorderLayout.SOUTH);

        ActionListener sendAction = e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                ChatClient.sendMessage(message);
                messageField.setText("");
            }
        };

        sendButton.addActionListener(sendAction);
        messageField.addActionListener(sendAction);

        frame.setVisible(true);
    }

    public void updateMessages() {
        StringBuilder messages = new StringBuilder();
        for (String message : ChatClient.getMessages()) {
            messages.append(message).append("\n");
        }
        messageArea.setText(messages.toString());
    }
}