package sg.rghis.android.disqus.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Category details
 */
public class Category implements Parcelable {

    @SerializedName("id")
    public long id;

    @SerializedName("forum")
    public String forum;

    @SerializedName("order")
    public int order;

    @SerializedName("isDefault")
    public boolean isDefault;

    @SerializedName("title")
    public String title;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.forum);
        dest.writeInt(this.order);
        dest.writeByte(isDefault ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
    }

    public Category() {
    }

    protected Category(Parcel in) {
        this.id = in.readLong();
        this.forum = in.readString();
        this.order = in.readInt();
        this.isDefault = in.readByte() != 0;
        this.title = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
