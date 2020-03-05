package abhishekwl.github.io.radar.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Patent implements Parcelable {

    private String patentNumber;
    private String patentTitle;
    private String patentAbstract;
    private String patentDate;
    private ArrayList<String> inventorsNames = new ArrayList<>();
    private String patentCountryCode;

    public Patent(String patentNumber, String patentTitle, String patentAbstract, String patentDate, ArrayList<String> inventorsNames, String patentCountryCode) {
        this.patentNumber = patentNumber;
        this.patentTitle = patentTitle;
        this.patentAbstract = patentAbstract;
        this.patentDate = patentDate;
        this.inventorsNames = inventorsNames;
        this.patentCountryCode = patentCountryCode;
    }

    public String getPatentNumber() {
        return patentNumber;
    }

    public void setPatentNumber(String patentNumber) {
        this.patentNumber = patentNumber;
    }

    public String getPatentTitle() {
        return patentTitle;
    }

    public void setPatentTitle(String patentTitle) {
        this.patentTitle = patentTitle;
    }

    public String getPatentAbstract() {
        return patentAbstract;
    }

    public void setPatentAbstract(String patentAbstract) {
        this.patentAbstract = patentAbstract;
    }

    public String getPatentDate() {
        return patentDate;
    }

    public void setPatentDate(String patentDate) {
        this.patentDate = patentDate;
    }

    public ArrayList<String> getInventorsNames() {
        return inventorsNames;
    }

    public void setInventorsNames(ArrayList<String> inventorsNames) {
        this.inventorsNames = inventorsNames;
    }

    public String getPatentCountryCode() {
        return patentCountryCode;
    }

    public void setPatentCountryCode(String patentCountryCode) {
        this.patentCountryCode = patentCountryCode;
    }

    @Override
    public String toString() {
        return "Patent{" +
                "patentNumber='" + patentNumber + '\'' +
                ", patentTitle='" + patentTitle + '\'' +
                ", patentAbstract='" + patentAbstract + '\'' +
                ", patentDate='" + patentDate + '\'' +
                ", inventorsNames=" + inventorsNames +
                ", patentCountryCode='" + patentCountryCode + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.patentNumber);
        dest.writeString(this.patentTitle);
        dest.writeString(this.patentAbstract);
        dest.writeString(this.patentDate);
        dest.writeStringList(this.inventorsNames);
        dest.writeString(this.patentCountryCode);
    }

    private Patent(Parcel in) {
        this.patentNumber = in.readString();
        this.patentTitle = in.readString();
        this.patentAbstract = in.readString();
        this.patentDate = in.readString();
        this.inventorsNames = in.createStringArrayList();
        this.patentCountryCode = in.readString();
    }

    public static final Parcelable.Creator<Patent> CREATOR = new Parcelable.Creator<Patent>() {
        @Override
        public Patent createFromParcel(Parcel source) {
            return new Patent(source);
        }

        @Override
        public Patent[] newArray(int size) {
            return new Patent[size];
        }
    };
}
