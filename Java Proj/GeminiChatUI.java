import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;

public class GeminiChatUI {
    private static final String API_KEY = "AIzaSyCOY9_oie8Ej4w-eyKbawqWKiqwecrIKwo";
    private static final List<String> conversationHistory = new ArrayList<>();
    private static final HttpClient client = HttpClient.newHttpClient();

    private static JTextArea chatArea;
    private static JTextField inputField;

    public static void main(String[] args) {
        // Create main frame
        JFrame frame = new JFrame("Gemini ChatBot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        // Header panel decoration
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 144, 255));
        headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 60));
        JLabel titleLabel = new JLabel("Gemini ChatBot");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Chat display area with styling
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setBackground(new Color(245, 245, 245));
        chatArea.setForeground(Color.DARK_GRAY);
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Input panel decoration
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 16));
        sendButton.setBackground(new Color(30, 144, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Send button action
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Enter key action
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Show UI
        frame.setVisible(true);
    }

    private static void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty())
            return;

        chatArea.append("You: " + userInput + "\n");
        inputField.setText("");

        try {
            String response = generateResponse(userInput);
            chatArea.append("AI: " + response + "\n");
        } catch (Exception ex) {
            chatArea.append("Error: Could not get a response\n");
        }
    }

    private static String generateResponse(String prompt) throws Exception {
        // Build conversation context
        StringBuilder context = new StringBuilder();
        for (String entry : conversationHistory) {
            context.append(entry).append("\n");
        }
        context.append("User: ").append(prompt);

        // Create JSON request body
        String requestBody = String.format(
                "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
                sanitizeForJson(context.toString()));

        // Create HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" 
                                + API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Send request and get response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse response (simplified parsing)
        String responseText = parseGeminiResponse(response.body());

        // Update conversation history
        conversationHistory.add("User: " + prompt);
        conversationHistory.add("AI: " + responseText);

        return responseText;
    }

    private static String sanitizeForJson(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    private static String parseGeminiResponse(String jsonResponse) {
        int textIndex = jsonResponse.indexOf("\"text\": \"");
        if (textIndex == -1)
            return "Error parsing response";

        int start = textIndex + 9;
        int end = jsonResponse.indexOf("\"", start);
        return jsonResponse.substring(start, end).replace("\\n", "\n");
    }
}
