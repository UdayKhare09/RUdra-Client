import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatClient {
    private static List<String> messages = new ArrayList<>();
    private static ClientGUI gui;
    private static PrintWriter out;

    public static void start(String SERVER_IP,String serverPassword, String userName) {
        final int SERVER_PORT = 8080;

        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            out.println(serverPassword);

            out.println(userName);

            gui = new ClientGUI();

            // Thread for receiving messages from the server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {

                        if (message.startsWith(userName + ":")) {
                        } else if (message.equals("You have been kicked from the server.")) {
                            System.out.println("You are kicked");
                            System.exit(0);
                        } else {
                            System.out.println(message);
                            messages.add(message);
                            gui.updateMessages();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            System.out.println("Connected to the chat. Type your messages (type '/exit' to leave):");

            // Thread for sending messages to the server
            new Thread(() -> {
                String userInput;
                while (true) {
                    userInput = scanner.nextLine();
                    // Check for the exit command
                    if ("/exit".equalsIgnoreCase(userInput)) {
                        System.out.println("You have left the chat.");
                        System.exit(0);
                    } else if (userInput.equals("/msglist")) {
                        getMessages().forEach(System.out::println);
                    } else {
                        messages.add("Sent: " + userInput);
                        gui.updateMessages();
                        out.println(userInput);
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getMessages() {
        return messages;
    }

    public static void sendMessage(String message) {
        messages.add("Sent: " + message);
        gui.updateMessages();
        out.println(message);
    }
}
