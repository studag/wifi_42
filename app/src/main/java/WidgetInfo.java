import android.graphics.Color;

public class WidgetInfo {

    private Color background_color;
    private Color text_color;
    private int appId;
    private String title;

    public Color getBackground_color() {
        return background_color;
    }

    public void setBackground_color(Color background_color) {
        this.background_color = background_color;
    }

    public Color getText_color() {
        return text_color;
    }

    public void setText_color(Color text_color) {
        this.text_color = text_color;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAsscoiated_ssid() {
        return asscoiated_ssid;
    }

    public void setAsscoiated_ssid(String asscoiated_ssid) {
        this.asscoiated_ssid = asscoiated_ssid;
    }

    private String asscoiated_ssid;

    public WidgetInfo(Color background_color, Color text_color, int appId, String title, String asscoiated_ssid) {
        this.background_color = background_color;
        this.text_color = text_color;
        this.appId = appId;
        this.title = title;
        this.asscoiated_ssid = asscoiated_ssid;
    }
}
