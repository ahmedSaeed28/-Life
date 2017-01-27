package saeed.life.Model;

public class NavItem {
    private String title;
    private int icon;

    public NavItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }
}
