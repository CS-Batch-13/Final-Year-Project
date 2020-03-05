package abhishekwl.github.io.radar.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class McxCommodity implements Parcelable {

    private String symbol;
    private double ltp;
    private double high;
    private double low;
    private double open;
    private double close;

    public McxCommodity(String symbol, double ltp, double high, double low, double open, double close) {
        this.symbol = symbol;
        this.ltp = ltp;
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getLtp() {
        return ltp;
    }

    public void setLtp(double ltp) {
        this.ltp = ltp;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    @Override
    public String toString() {
        return "McxCommodity{" +
                "symbol='" + symbol + '\'' +
                ", ltp=" + ltp +
                ", high=" + high +
                ", low=" + low +
                ", open=" + open +
                ", close=" + close +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.symbol);
        dest.writeDouble(this.ltp);
        dest.writeDouble(this.high);
        dest.writeDouble(this.low);
        dest.writeDouble(this.open);
        dest.writeDouble(this.close);
    }

    private McxCommodity(Parcel in) {
        this.symbol = in.readString();
        this.ltp = in.readDouble();
        this.high = in.readDouble();
        this.low = in.readDouble();
        this.open = in.readDouble();
        this.close = in.readDouble();
    }

    public static final Parcelable.Creator<McxCommodity> CREATOR = new Parcelable.Creator<McxCommodity>() {
        @Override
        public McxCommodity createFromParcel(Parcel source) {
            return new McxCommodity(source);
        }

        @Override
        public McxCommodity[] newArray(int size) {
            return new McxCommodity[size];
        }
    };
}
