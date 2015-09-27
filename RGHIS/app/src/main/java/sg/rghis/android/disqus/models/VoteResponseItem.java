package sg.rghis.android.disqus.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class VoteResponseItem implements Parcelable {
    @SerializedName("vote")
    int vote;

    @SerializedName("likesDelta")
    int likesDelta;

    @SerializedName("dislikesDelta")
    int dislikesDelta;

    @SerializedName("delta")
    int delta;

    @SerializedName("post")
    Post post;

    public VoteResponseItem(){
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public int getLikesDelta() {
        return likesDelta;
    }

    public void setLikesDelta(int likesDelta) {
        this.likesDelta = likesDelta;
    }

    public int getDislikesDelta() {
        return dislikesDelta;
    }

    public void setDislikesDelta(int dislikesDelta) {
        this.dislikesDelta = dislikesDelta;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.vote);
        dest.writeInt(this.likesDelta);
        dest.writeInt(this.dislikesDelta);
        dest.writeInt(this.delta);
        dest.writeParcelable(this.post, 0);
    }

    protected VoteResponseItem(Parcel in) {
        this.vote = in.readInt();
        this.likesDelta = in.readInt();
        this.dislikesDelta = in.readInt();
        this.delta = in.readInt();
        this.post = in.readParcelable(Post.class.getClassLoader());
    }

    public static final Parcelable.Creator<VoteResponseItem> CREATOR = new Parcelable.Creator<VoteResponseItem>() {
        public VoteResponseItem createFromParcel(Parcel source) {
            return new VoteResponseItem(source);
        }

        public VoteResponseItem[] newArray(int size) {
            return new VoteResponseItem[size];
        }
    };
}
