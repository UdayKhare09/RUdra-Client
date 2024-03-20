import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatClient {

    private static ClientGUI gui;
    private static UserList userList = new UserList();
    private static PrintWriter out;
    private static String userName;

    public static void start(String SERVER_IP, String serverPassword, String userName) {
        final int SERVER_PORT = 8080;
        ChatClient.userName = userName;

        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);



            out.println(serverPassword);

            out.println(userName);

            // Create the GUI
            gui = new ClientGUI();

            String userListFirst = in.readLine();
            String[] userListArray = userListFirst.split(",");
            for (String user : userListArray) {
                userList.addUser(user);
            }

            updateUserList();

            // Thread for receiving messages from the server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
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
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<String> getUserMessages(String name) {
        return userList.getUserMessages(name);
    }
    public static void updateUserList() {gui.updateUserList(userList.getAllUserNames());}
    public static void sendMessage(String user, String message) {
        out.println(user + "^" + message);
        userList.addUserMessage(userName, "You: " + message);
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
        System.out.println("User names: " + userNames);
        return userNames;
    }
}