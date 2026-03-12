import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClient {

    BufferedReader in;
    PrintWriter out;
    String username;

    JFrame frame = new JFrame("Chat Application");

    JTextPane messagePane = new JTextPane();
    JTextField textField = new JTextField(40);
    JButton sendButton = new JButton("Send");

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public ChatClient() {

        username = JOptionPane.showInputDialog(
                frame,
                "Enter your username:",
                "Username",
                JOptionPane.PLAIN_MESSAGE
        );

        if (username == null || username.trim().isEmpty()) {
            username = "User" + (int)(Math.random()*1000);
        }

        messagePane.setEditable(false);
        messagePane.setBackground(new Color(240,248,255));
        messagePane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(messagePane);

        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(Color.WHITE);

        sendButton.setBackground(new Color(0,153,255));
        sendButton.setForeground(Color.WHITE);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(200,220,255));
        bottomPanel.add(textField);
        bottomPanel.add(sendButton);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(210,230,255));

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        frame.setSize(650,450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        sendButton.addActionListener(e -> sendMessage());
        textField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {

        String message = textField.getText();

        if(!message.isEmpty()) {

            out.println(message);
            textField.setText("");

        }
    }

    private void appendMessage(String message) {

        StyledDocument doc = messagePane.getStyledDocument();

        Style bubble = messagePane.addStyle("bubble", null);

        String time = timeFormat.format(new Date());

        try {

            if(message.startsWith("Server:")){

                StyleConstants.setForeground(bubble, Color.BLUE);
                StyleConstants.setBackground(bubble, new Color(220,230,255));

            }

            else if(message.contains("(private)")){

                StyleConstants.setForeground(bubble, new Color(150,0,200));
                StyleConstants.setBackground(bubble, new Color(240,220,255));

            }

            else if(message.startsWith(username + ":")){

                StyleConstants.setForeground(bubble, new Color(0,120,0));
                StyleConstants.setBackground(bubble, new Color(210,255,210));

            }

            else{

                StyleConstants.setForeground(bubble, Color.BLACK);
                StyleConstants.setBackground(bubble, new Color(235,235,235));

            }

            StyleConstants.setLeftIndent(bubble, 10);
            StyleConstants.setRightIndent(bubble, 10);
            StyleConstants.setSpaceAbove(bubble, 5);
            StyleConstants.setSpaceBelow(bubble, 5);

            doc.insertString(doc.getLength(), "[" + time + "] " + message + "\n", bubble);

        } catch(Exception e){}
    }

    private void run() throws Exception {

        Socket socket = new Socket("localhost", 5000);

        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        out = new PrintWriter(
                socket.getOutputStream(), true);

        out.println(username);

        String line;

        while((line = in.readLine()) != null){

            appendMessage(line);

        }
    }

    public static void main(String[] args) throws Exception {

        ChatClient client = new ChatClient();
        client.frame.setVisible(true);
        client.run();

    }
}