package sg.rghis.android.rss.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.convert.Convert;

import java.util.ArrayList;
import java.util.List;

import sg.rghis.android.rss.EmptyElementConverter;

public class Channel implements Parcelable {
    @Element
    String title;

    @Element
    String link;

    @Element(name = "description", required = false)
    @Convert(value = EmptyElementConverter.class)
    String description;

    @ElementList(name = "item", required = true, inline = true)
    List<Item> items;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.link);
        dest.writeString(this.description);
        dest.writeList(this.items);
    }

    public Channel() {
    }

    protected Channel(Parcel in) {
        this.title = in.readString();
        this.link = in.readString();
        this.description = in.readString();
        this.items = new ArrayList<Item>();
        in.readList(this.items, List.class.getClassLoader());
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        public Channel createFromParcel(Parcel source) {
            return new Channel(source);
        }

        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}
