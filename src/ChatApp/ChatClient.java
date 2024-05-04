package ChatApp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatClient {

    private static ClientGUI gui;
    private static UserList userList = new UserList();
    private static PrintWriter out;
    private static String userName;
    private static final String ALGORITHM = "AES";
    private static final String encryptionKey = "SixteenByteKey12";
    private static Socket socket;

    public static void start(String SERVER_IP, String serverPassword, String userName) {
        final int SERVER_PORT = 7415;
        ChatClient.userName = userName;

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);



            out.println(encrypt(serverPassword));

            out.println(encrypt(userName));

            String response = decrypt(in.readLine());

            switch (response) {
                case "WrongServerPassword" -> {
                    JOptionPane.showMessageDialog(null, "Wrong server password. Please try again.");
                    System.exit(0);
                }
                case "UserNotFound" -> {
                    JOptionPane.showMessageDialog(null, "ChatApp.User not found. Please try again.");
                    System.exit(0);
                }
                case "Authenticated" -> System.out.println("Authenticated");
            }

            // Create the GUI
            gui = new ClientGUI();

            String userListFirst = decrypt(in.readLine());
            String[] userListArray = userListFirst.split(",");
            for (String user : userListArray) {
                userList.addUser(user);
            }

            updateUserList();

            // Thread for receiving messages from the server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = decrypt(in.readLine())) != null) {
                        String[] parts = message.split("\\^");
                        if (parts.length == 2) {
                            String sender = parts[0];
                            System.out.println(sender);
                            String msg = parts[1];
                            System.out.println(msg);
                            if (sender.equals("connected")){
                                // Add the user to the user list
                                userList.addUser(msg);
                                updateUserList();
                            } else if (sender.equals("disconnected")){
                                // Remove the user from the user list
                                userList.removeUser(msg);
                                updateUserList();
                            } else {
                                // Add the message to the user's message list
                                userList.addUserMessage(sender, sender+"- "+msg);
                                // Update the message area in the GUI
                                SwingUtilities.invokeLater(() -> {
                                    List<String> messages = getUserMessages(sender);
                                    if (messages != null) {
                                        gui.updateMessageArea(String.join("\n", messages));
                                    } else {
                                        gui.updateMessageArea("No messages for this user.");
                                    }
                                });
                            }
                        } else if (parts.length == 3) {
                            String sender = parts[0];
                            String msg = parts[1];
                            String type = parts[2];
                            if (type.equals("image")) {
                                byte[] imageBytes = Base64.getDecoder().decode(msg);
                                ImageIcon image = new ImageIcon(imageBytes);
                                new ImageFrame(image, sender);
                                userList.addUserMessage(sender, sender+"- "+type);
                                SwingUtilities.invokeLater(() -> {
                                    List<String> messages = getUserMessages(sender);
                                    if (messages != null) {
                                        gui.updateMessageArea(String.join("\n", messages));
                                    } else {
                                        gui.updateMessageArea("No messages for this user.");
                                    }
                                });
                            } else if (type.equals("voice")) {
                                playVoiceMessage(msg);
                                userList.addUserMessage(sender, sender+"- "+type);
                                SwingUtilities.invokeLater(() -> {
                                    List<String> messages = getUserMessages(sender);
                                    if (messages != null) {
                                        gui.updateMessageArea(String.join("\n", messages));
                                    } else {
                                        gui.updateMessageArea("No messages for this user.");
                                    }
                                });
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static List<String> getUserMessages(String name) {
        return userList.getUserMessages(name);
    }
    public static void updateUserList() {gui.updateUserList(userList.getAllUserNames());}
    public static void sendMessage(String user, String message) {
        try {
            out.println(encrypt(user + "^" + message));
            userList.addUserMessage(userName, "You: " + message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String value) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedValue);
    }

    public static String decrypt(String encryptedValue) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        byte[] originalValue = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
        return new String(originalValue, StandardCharsets.UTF_8);
    }

    public static void sendVoiceMessage(String user) {
        try {
            // Record voice
            AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            ByteArrayOutputStream outV = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            long start = System.currentTimeMillis();
            while ((bytesRead = microphone.read(buffer, 0, buffer.length)) > 0 && System.currentTimeMillis() - start < 3000) {
                outV.write(buffer, 0, bytesRead);
            }
            microphone.close();

            // Encode voice message
            byte[] audioBytes = outV.toByteArray();
            String encodedVoiceMessage = Base64.getEncoder().encodeToString(audioBytes);

            // Send voice message
            out.println(encrypt(user + "^" + encodedVoiceMessage + "^voice"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void playVoiceMessage(String encodedVoiceMessage) throws Exception {
        // Decode voice message
        byte[] audioBytes = Base64.getDecoder().decode(encodedVoiceMessage);
        ByteArrayInputStream in = new ByteArrayInputStream(audioBytes);
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        AudioInputStream audioInputStream = new AudioInputStream(in, format, audioBytes.length);

        // Play voice message
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine speaker = (SourceDataLine) AudioSystem.getLine(info);
        speaker.open(format);
        speaker.start();
        int bytesRead;
        byte[] buffer = new byte[1024];
        while ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) > 0) {
            speaker.write(buffer, 0, bytesRead);
        }
        speaker.drain();
        speaker.close();
    }
}

class User {
    private String name;
    private List<String> messages;

    public User(String name) {
        this.name = name;
        this.messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void addMessage(String message) {
        messages.add(message);
    }
}

class UserList {
    private Map<String, User> users;

    public UserList() {
        this.users = new HashMap<>();
    }

    public void addUser(String name) {
        users.put(name, new User(name));
    }

    public void removeUser(String name) {
        users.remove(name);
    }

    public List<String> getUserMessages(String name) {
        User user = users.get(name);
        return user != null ? user.getMessages() : null;
    }

    public void addUserMessage(String name, String message) {
        User user = users.get(name);
        if (user != null) {
            user.addMessage(message);
        }
    }

    public List<String> getAllUserNames() {
        List<String> userNames = new ArrayList<>(users.keySet());
        System.out.println("ChatApp.User names: " + userNames);
        return userNames;
    }
}