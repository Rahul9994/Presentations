import javax.swing.*;                                   //  Import Statements
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class BrowserHistoryManagerGUI {                     // Class Declaration and Member Variables

    private JFrame frame;                              
    private JTextField urlField, titleField, searchField;
    private JTextArea displayArea;
    private JLabel currentPageLabel;
    private JDialog historyDialog, bookmarkDialog;

    private History history = new History();

    public BrowserHistoryManagerGUI() {                       // Constructor: Building the GUI
        frame = new JFrame("Browser History Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        urlField = new JTextField(20);             //Creating and configuring Components
        titleField = new JTextField(20);
        searchField = new JTextField(20);

        JButton visitButton = new JButton("Visit Page");
        JButton backButton = new JButton("Go Back");
        JButton forwardButton = new JButton("Go Forward");
        JButton undoButton = new JButton("Undo");
        JButton redoButton = new JButton("Redo");
        JButton bookmarkButton = new JButton("Bookmark");
        JButton searchButton = new JButton("Search");
        JButton viewHistoryButton = new JButton("View History");
        JButton clearHistoryButton = new JButton("Clear History");
        JButton viewBookmarksButton = new JButton("Go to Bookmarks");

        currentPageLabel = new JLabel("Current Page: None", SwingConstants.CENTER);
        currentPageLabel.setFont(new Font("Serif", Font.BOLD, 18));
        
        displayArea = new JTextArea(10, 50);
        displayArea.setEditable(false);
        
        JPanel inputPanel = new JPanel();                   //  Arranging Components in Panels and Adding Them to the Frame
        inputPanel.add(new JLabel("URL:"));
        inputPanel.add(urlField);
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(visitButton);
        inputPanel.add(backButton);
        inputPanel.add(forwardButton);
        inputPanel.add(undoButton);
        inputPanel.add(redoButton);
        inputPanel.add(bookmarkButton);
        
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(viewHistoryButton);
        searchPanel.add(clearHistoryButton);
        searchPanel.add(viewBookmarksButton);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(currentPageLabel, BorderLayout.CENTER);
        frame.add(searchPanel, BorderLayout.SOUTH);

        visitButton.addActionListener(new VisitPageListener());     //Wiring Up Event Listeners
        backButton.addActionListener(new GoBackListener());
        forwardButton.addActionListener(new GoForwardListener());
        undoButton.addActionListener(new UndoListener());
        redoButton.addActionListener(new RedoListener());
        bookmarkButton.addActionListener(new BookmarkListener());
        searchButton.addActionListener(new SearchListener());
        viewHistoryButton.addActionListener(new ViewHistoryListener());
        clearHistoryButton.addActionListener(new ClearHistoryListener());
        viewBookmarksButton.addActionListener(new ViewBookmarksListener());

        frame.setVisible(true);                                      // Finalizing the GUI
    }

    private void updateDisplay() {                                     // Updating the Display
        Page currentPage = history.getCurrentPage();
        currentPageLabel.setText("Current Page: " + (currentPage != null ? currentPage.toString() : "None"));
    }

    class VisitPageListener implements ActionListener {              // Inner Classes (Event Listeners)
        @Override
        public void actionPerformed(ActionEvent e) {
            String url = urlField.getText();
            String title = titleField.getText();
            if (!url.isEmpty() && !title.isEmpty()) {
                history.visitPage(url, title);
                updateDisplay();
                urlField.setText("");
                titleField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter both URL and Title.");
            }
        }
    }

    class GoBackListener implements ActionListener {   
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!history.goBack()) {
                JOptionPane.showMessageDialog(frame, "Cannot go back.");
            }
            updateDisplay();
        }
    }

    class GoForwardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!history.goForward()) {
                JOptionPane.showMessageDialog(frame, "Cannot go forward.");
            }
            updateDisplay();
        }
    }

    class UndoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!history.undo()) {
                JOptionPane.showMessageDialog(frame, "Nothing to undo.");
            }
            updateDisplay();
        }
    }

    class RedoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!history.redo()) {
                JOptionPane.showMessageDialog(frame, "Nothing to redo.");
            }
            updateDisplay();
        }
    }

    class BookmarkListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!history.bookmarkPage()) {
                JOptionPane.showMessageDialog(frame, "No current page to bookmark.");
            }
        }
    }

    class SearchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String keyword = searchField.getText();
            if (!keyword.isEmpty()) {
                String searchResults = history.searchHistory(keyword);
                showDialog("Search Results", searchResults);
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a keyword to search.");
            }
        }
    }

    class ViewHistoryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showDialog("Browsing History", history.viewHistory());
        }
    }

    class ClearHistoryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            history.clearHistory();
            updateDisplay();
        }
    }

    class ViewBookmarksListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder bookmarks = new StringBuilder();
            for (Page bookmark : history.getBookmarks()) {
                bookmarks.append(bookmark).append("\n");
            }
            showDialog("Bookmarks", bookmarks.toString());
        }
    }

    private void showDialog(String title, String content) {
        JDialog dialog = new JDialog(frame, title, true);
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        dialog.add(new JScrollPane(textArea));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
}
