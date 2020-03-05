package abhishekwl.github.io.radar.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Post implements Parcelable {

    private String title;
    private String author;
    private String content;
    private String description;
    private String image;
    private String sourceId;
    private String sourceName;
    private String url;
    private ArrayList<String> linksArrayList;

    public Post(String title, String author, String content, String description, String image, String sourceId, String sourceName, String url, ArrayList<String> linksArrayList) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.description = description;
        this.image = image;
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.url = url;
        this.linksArrayList = linksArrayList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<String> getLinksArrayList() {
        return linksArrayList;
    }

    public void setLinksArrayList(ArrayList<String> linksArrayList) {
        this.linksArrayList = linksArrayList;
    }

    @Override
    public String toString() {
        return "Post{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", url='" + url + '\'' +
                ", linksArrayList=" + linksArrayList +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.content);
        dest.writeString(this.description);
        dest.writeString(this.image);
        dest.writeString(this.sourceId);
        dest.writeString(this.sourceName);
        dest.writeString(this.url);
        dest.writeStringList(this.linksArrayList);
    }

    private Post(Parcel in) {
        this.title = in.readString();
        this.author = in.readString();
        this.content = in.readString();
        this.description = in.readString();
        this.image = in.readString();
        this.sourceId = in.readString();
        this.sourceName = in.readString();
        this.url = in.readString();
        this.linksArrayList = in.createStringArrayList();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
