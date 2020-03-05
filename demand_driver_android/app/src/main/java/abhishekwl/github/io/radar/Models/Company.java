package abhishekwl.github.io.radar.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Company implements Parcelable {

    private int id;
    private String name;
    private String url;
    private String twitter;
    private String youtube;
    private String facebook;
    private String google;
    private String hqCountry;
    private String hqState;
    private String linkedIn;
    private String logo;

    public Company(int id, String name, String url, String twitter, String youtube, String facebook, String google, String hqCountry, String hqState, String linkedIn, String logo) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.twitter = twitter;
        this.youtube = youtube;
        this.facebook = facebook;
        this.google = google;
        this.hqCountry = hqCountry;
        this.hqState = hqState;
        this.linkedIn = linkedIn;
        this.logo = "https:"+logo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getGoogle() {
        return google;
    }

    public void setGoogle(String google) {
        this.google = google;
    }

    public String getHqCountry() {
        return hqCountry;
    }

    public void setHqCountry(String hqCountry) {
        this.hqCountry = hqCountry;
    }

    public String getHqState() {
        return hqState;
    }

    public void setHqState(String hqState) {
        this.hqState = hqState;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", twitter='" + twitter + '\'' +
                ", youtube='" + youtube + '\'' +
                ", facebook='" + facebook + '\'' +
                ", google='" + google + '\'' +
                ", hqCountry='" + hqCountry + '\'' +
                ", hqState='" + hqState + '\'' +
                ", linkedIn='" + linkedIn + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.twitter);
        dest.writeString(this.youtube);
        dest.writeString(this.facebook);
        dest.writeString(this.google);
        dest.writeString(this.hqCountry);
        dest.writeString(this.hqState);
        dest.writeString(this.linkedIn);
        dest.writeString(this.logo);
    }

    private Company(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.url = in.readString();
        this.twitter = in.readString();
        this.youtube = in.readString();
        this.facebook = in.readString();
        this.google = in.readString();
        this.hqCountry = in.readString();
        this.hqState = in.readString();
        this.linkedIn = in.readString();
        this.logo = in.readString();
    }

    public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel source) {
            return new Company(source);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };
}
