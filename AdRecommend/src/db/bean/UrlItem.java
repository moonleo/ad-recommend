package db.bean;

public class UrlItem {
    private int id;
    private String url;

    public UrlItem() {

    }

    public UrlItem(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
