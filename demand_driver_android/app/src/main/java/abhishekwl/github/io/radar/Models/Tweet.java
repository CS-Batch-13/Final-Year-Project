package abhishekwl.github.io.radar.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Tweet implements Parcelable {

    private String userName;
    private int userFollowersCount;
    private String userImage;
    private String content;
    private String createdAtTime;
    private int retweetCount;

    public Tweet(String userName, int userFollowersCount, String userImage, String content, String createdAtTime, int retweetCount) {
        this.userName = userName;
        this.userFollowersCount = userFollowersCount;
        this.userImage = userImage;
        this.content = content;
        this.createdAtTime = createdAtTime;
        this.retweetCount = retweetCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserFollowersCount() {
        return userFollowersCount;
    }

    public void setUserFollowersCount(int userFollowersCount) {
        this.userFollowersCount = userFollowersCount;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAtTime() {
        return createdAtTime;
    }

    public void setCreatedAtTime(String createdAtTime) {
        this.createdAtTime = createdAtTime;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "userName='" + userName + '\'' +
                ", userFollowersCount=" + userFollowersCount +
                ", userImage='" + userImage + '\'' +
                ", content='" + content + '\'' +
                ", createdAtTime='" + createdAtTime + '\'' +
                ", retweetCount=" + retweetCount +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeInt(this.userFollowersCount);
        dest.writeString(this.userImage);
        dest.writeString(this.content);
        dest.writeString(this.createdAtTime);
        dest.writeInt(this.retweetCount);
    }

    protected Tweet(Parcel in) {
        this.userName = in.readString();
        this.userFollowersCount = in.readInt();
        this.userImage = in.readString();
        this.content = in.readString();
        this.createdAtTime = in.readString();
        this.retweetCount = in.readInt();
    }

    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
