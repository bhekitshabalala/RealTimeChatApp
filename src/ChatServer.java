import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static final int PORT = 5000;
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {

        System.out.println("Chat Server is running on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {

                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {

            try {

                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                out = new PrintWriter(
                        socket.getOutputStream(), true);

                username = in.readLine();

                synchronized (clients) {
                    clients.put(username, out);
                }

                broadcast("Server: " + username + " joined the chat.");

                String message;

                while ((message = in.readLine()) != null) {

                    if (message.startsWith("@")) {

                        int space = message.indexOf(" ");

                        if (space != -1) {

                            String target = message.substring(1, space);
                            String privateMsg = message.substring(space + 1);

                            sendPrivate(target,
                                    username + " (private): " + privateMsg);
                        }

                    } else {

                        broadcast(username + ": " + message);

                    }

                }

            } catch (Exception e) {

                System.out.println("Connection closed.");

            } finally {

                if (username != null) {

                    synchronized (clients) {
                        clients.remove(username);
                    }

                    broadcast("Server: " + username + " left the chat.");
                }

                try {
                    socket.close();
                } catch (Exception e) {
                }
            }
        }

        private void broadcast(String message) {

            synchronized (clients) {

                for (PrintWriter writer : clients.values()) {

                    writer.println(message);

                }

            }

            System.out.println(message);
        }

        private void sendPrivate(String user, String message) {

            synchronized (clients) {

                PrintWriter writer = clients.get(user);

                if (writer != null) {

                    writer.println(message);
                    out.println(message);

                } else {

                    out.println("Server: User not found.");

                }

            }
        }
    }
}