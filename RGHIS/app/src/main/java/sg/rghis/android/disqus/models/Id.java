package sg.rghis.android.disqus.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Id implements Parcelable {

    @SerializedName("id")
    private long id;

    public Id() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
    }

    protected Id(Parcel in) {
        this.id = in.readLong();
    }

    public static final Creator<Id> CREATOR = new Creator<Id>() {
        public Id createFromParcel(Parcel source) {
            return new Id(source);
        }

        public Id[] newArray(int size) {
            return new Id[size];
        }
    };
}
