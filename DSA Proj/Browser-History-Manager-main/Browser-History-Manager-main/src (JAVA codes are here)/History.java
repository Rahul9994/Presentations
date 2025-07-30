import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class History {
    private Page currentPage;
    private Stack<Page> undoStack = new Stack<>();
    private Stack<Page> redoStack = new Stack<>();
    private List<Page> bookmarks = new ArrayList<>();

    public void visitPage(String url, String title) {
        Page newPage = new Page(url, title);
        if (currentPage != null) {
            undoStack.push(currentPage);
            currentPage.next = newPage;
            newPage.prev = currentPage;
        }
        currentPage = newPage;
        redoStack.clear();
    }

    public boolean goBack() {
        if (currentPage != null && currentPage.prev != null) {
            redoStack.push(currentPage);
            currentPage = currentPage.prev;
            return true;
        }
        return false;
    }

    public boolean goForward() {
        if (currentPage != null && currentPage.next != null) {
            undoStack.push(currentPage);
            currentPage = currentPage.next;
            return true;
        }
        return false;
    }

    public boolean undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(currentPage);
            currentPage = undoStack.pop();
            return true;
        }
        return false;
    }

    public boolean redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(currentPage);
            currentPage = redoStack.pop();
            return true;
        }
        return false;
    }

    public boolean bookmarkPage() {
        if (currentPage != null) {
            currentPage.bookmark();
            bookmarks.add(currentPage);
            return true;
        }
        return false;
    }

    public void clearHistory() {
        currentPage = null;
        undoStack.clear();
        redoStack.clear();
        bookmarks.clear();
    }

    public String searchHistory(String keyword) {
        if (currentPage == null) {
            return "No browsing history.";
        }
        
        StringBuilder result = new StringBuilder();
        Page temp = currentPage;
        while (temp.prev != null) {
            temp = temp.prev;
        }

        while (temp != null) {
            if (temp.url.contains(keyword) || temp.title.contains(keyword)) {
                result.append(temp).append("\n");
            }
            temp = temp.next;
        }
        return result.toString().isEmpty() ? "No matching pages found." : result.toString();
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public String viewHistory() {
        if (currentPage == null) {
            return "No browsing history.";
        }
        
        StringBuilder history = new StringBuilder();
        Page temp = currentPage;
        
        // Traverse back to the first page in history
        while (temp.prev != null) {
            temp = temp.prev;
        }
        
        // Build the history display by moving forward through the pages
        while (temp != null) {
            history.append(temp).append("\n");
            temp = temp.next;
        }
        return history.toString();
    }

    public List<Page> getBookmarks() {
        return bookmarks;
    }
}
