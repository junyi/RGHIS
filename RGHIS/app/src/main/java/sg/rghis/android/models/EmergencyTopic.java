package sg.rghis.android.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EmergencyTopic extends RealmObject {
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String HTML = "html";

    @PrimaryKey
    private String title;
    private String url;
    private String html;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
