class Page {
    String url;
    String title;
    Page prev;
    Page next;
    boolean isBookmarked;

    public Page(String url, String title) {
        this.url = url;
        this.title = title;
        this.isBookmarked = false;
    }

    public void bookmark() {
        this.isBookmarked = true;
    }

    @Override
    public String toString() {
        return title + " (" + url + ")" + (isBookmarked ? " [Bookmarked]" : "");
    }
}