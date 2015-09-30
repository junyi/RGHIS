package sg.rghis.android.rss.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Rss implements Parcelable {
    @Element
    Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.channel, 0);
    }

    public Rss() {
    }

    protected Rss(Parcel in) {
        this.channel = in.readParcelable(Channel.class.getClassLoader());
    }

    public static final Creator<Rss> CREATOR = new Creator<Rss>() {
        public Rss createFromParcel(Parcel source) {
            return new Rss(source);
        }

        public Rss[] newArray(int size) {
            return new Rss[size];
        }
    };
}
