package abhishekwl.github.io.radar.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Future implements Parcelable {

    private String title;
    private String code;

    public Future(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Future{" +
                "title='" + title + '\'' +
                ", code='" + code + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.code);
    }

    private Future(Parcel in) {
        this.title = in.readString();
        this.code = in.readString();
    }

    public static final Parcelable.Creator<Future> CREATOR = new Parcelable.Creator<Future>() {
        @Override
        public Future createFromParcel(Parcel source) {
            return new Future(source);
        }

        @Override
        public Future[] newArray(int size) {
            return new Future[size];
        }
    };
}
