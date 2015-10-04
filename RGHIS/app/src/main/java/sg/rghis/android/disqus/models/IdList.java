package sg.rghis.android.disqus.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class IdList implements Parcelable {

    @SerializedName("code")
    long code;

    @SerializedName("response")
    ArrayList<Id> responseData;

    public long getCode() {
        return code;
    }

    public ArrayList<Id> getResponseData() {
        return new ArrayList<>(responseData); // return a copy not own so data is preserved
    }

    public IdList() {
    }

    public IdList(long code, ArrayList<Id> responseData) {
        this.code = code;
        this.responseData = responseData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.code);
        dest.writeList(this.responseData);
    }

    private IdList(Parcel in) {
        this.code = in.readLong();
        in.readList(this.responseData, Entity.class.getClassLoader());
    }

    public static final Creator<IdList> CREATOR = new Creator<IdList>() {
        public IdList createFromParcel(Parcel source) {
            return new IdList(source);
        }

        public IdList[] newArray(int size) {
            return new IdList[size];
        }
    };
}
