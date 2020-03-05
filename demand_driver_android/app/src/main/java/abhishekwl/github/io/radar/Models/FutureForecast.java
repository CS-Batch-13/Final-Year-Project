package abhishekwl.github.io.radar.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class FutureForecast implements Parcelable {

    private double high;
    private double low;
    private double lastPrice;
    private double percentChange;
    private String symbol;
    private String name;

    public FutureForecast(double high, double low, double lastPrice, double percentChange, String symbol, String name) {
        this.high = high;
        this.low = low;
        this.lastPrice = lastPrice;
        this.percentChange = percentChange;
        this.symbol = symbol;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public void setLastPrice(int lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "FutureForecast{" +
                "high=" + high +
                ", low=" + low +
                ", lastPrice=" + lastPrice +
                ", percentChange=" + percentChange +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.high);
        dest.writeDouble(this.low);
        dest.writeDouble(this.lastPrice);
        dest.writeDouble(this.percentChange);
        dest.writeString(this.symbol);
        dest.writeString(this.name);
    }

    private FutureForecast(Parcel in) {
        this.high = in.readDouble();
        this.low = in.readDouble();
        this.lastPrice = in.readDouble();
        this.percentChange = in.readDouble();
        this.symbol = in.readString();
        this.name = in.readString();
    }

    public static final Creator<FutureForecast> CREATOR = new Creator<FutureForecast>() {
        @Override
        public FutureForecast createFromParcel(Parcel source) {
            return new FutureForecast(source);
        }

        @Override
        public FutureForecast[] newArray(int size) {
            return new FutureForecast[size];
        }
    };
}
