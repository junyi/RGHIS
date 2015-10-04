package sg.rghis.android.disqus.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Thread
 */
public class AuthorlessThread implements Parcelable {

    @SerializedName("feed")
    public String feed;

    @SerializedName("category")
    public String category;

    @SerializedName("identifiers")
    public List<String> identifiers;

    public transient Object forum;

    @SerializedName("clean_title")
    public String cleanTitle;

    @SerializedName("dislikes")
    public int dislikes;

    @SerializedName("isDeleted")
    public boolean isDeleted;

    @SerializedName("author")
    public String author;

    @SerializedName("userScore")
    public int userScore;

    @SerializedName("raw_message")
    public String rawMessage;

    @SerializedName("id")
    public long id;

    @SerializedName("isClosed")
    public boolean isClosed;

    @SerializedName("posts")
    public int posts;

    @SerializedName("userSubscription")
    public boolean userSubscription;

    @SerializedName("link")
    public String link;

    @SerializedName("createdAt")
    public Date createdAt;

    @SerializedName("title")
    public String title;

    @SerializedName("message")
    public String message;

    @SerializedName("slug")
    public String slug;

    @SerializedName("highlightedPost")
    public String highlightedPost;

    @SerializedName("likes")
    public int likes;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.feed);
        dest.writeString(this.category);
        dest.writeStringList(this.identifiers);
        dest.writeString(this.cleanTitle);
        dest.writeInt(this.dislikes);
        dest.writeByte(isDeleted ? (byte) 1 : (byte) 0);
        dest.writeString(this.author);
        dest.writeInt(this.userScore);
        dest.writeString(this.rawMessage);
        dest.writeLong(this.id);
        dest.writeByte(isClosed ? (byte) 1 : (byte) 0);
        dest.writeInt(this.posts);
        dest.writeByte(userSubscription ? (byte) 1 : (byte) 0);
        dest.writeString(this.link);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeString(this.title);
        dest.writeString(this.message);
        dest.writeString(this.slug);
        dest.writeString(this.highlightedPost);
        dest.writeInt(this.likes);
    }

    public AuthorlessThread() {
    }

    protected AuthorlessThread(Parcel in) {
        this.feed = in.readString();
        this.category = in.readString();
        this.identifiers = in.createStringArrayList();
        this.cleanTitle = in.readString();
        this.dislikes = in.readInt();
        this.isDeleted = in.readByte() != 0;
        this.author = in.readString();
        this.userScore = in.readInt();
        this.rawMessage = in.readString();
        this.id = in.readLong();
        this.isClosed = in.readByte() != 0;
        this.posts = in.readInt();
        this.userSubscription = in.readByte() != 0;
        this.link = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.title = in.readString();
        this.message = in.readString();
        this.slug = in.readString();
        this.highlightedPost = in.readString();
        this.likes = in.readInt();
    }

    public static final Creator<AuthorlessThread> CREATOR = new Creator<AuthorlessThread>() {
        public AuthorlessThread createFromParcel(Parcel source) {
            return new AuthorlessThread(source);
        }

        public AuthorlessThread[] newArray(int size) {
            return new AuthorlessThread[size];
        }
    };
}