package abhishekwl.github.io.radar.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import abhishekwl.github.io.radar.Models.FutureForecast;
import abhishekwl.github.io.radar.R;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AffectedCommoditiesRecyclerViewAdapter extends RecyclerView.Adapter<AffectedCommoditiesRecyclerViewAdapter.AffectedCommodityViewHolder> {

    private ArrayList<FutureForecast> futureForecastArrayList;
    private Context context;

    public AffectedCommoditiesRecyclerViewAdapter(ArrayList<FutureForecast> futureForecastArrayList, Context context) {
        this.futureForecastArrayList = futureForecastArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public AffectedCommoditiesRecyclerViewAdapter.AffectedCommodityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.analyze_post_affected_commodities_list_item, viewGroup, false);
        return new AffectedCommodityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AffectedCommoditiesRecyclerViewAdapter.AffectedCommodityViewHolder affectedCommodityViewHolder, int i) {
        FutureForecast futureForecast = futureForecastArrayList.get(i);
        affectedCommodityViewHolder.bind(futureForecast, affectedCommodityViewHolder.itemView.getContext());
    }

    @Override
    public int getItemCount() {
        return futureForecastArrayList.size();
    }

    class AffectedCommodityViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.affectedCommoditiesListItemImageView)
        ImageView trendingUpOrDownImageView;
        @BindView(R.id.affectedCommoditiesListItemNameTextView)
        TextView commodityNameTextView;
        @BindView(R.id.affectedCommoditiesListItemPercentageChangeTextView)
        TextView percentageTextView;
        @BindColor(R.color.colorAccent)
        int colorAccent;
        @BindColor(R.color.colorPrimary)
        int colorPrimary;
        @BindColor(R.color.colorDanger)
        int colorDanger;
        @BindColor(R.color.colorGreen)
        int colorGreen;
        @BindColor(R.color.colorTextDark)
        int colorTextDark;

        AffectedCommodityViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void bind(FutureForecast futureForecast, Context context) {
            commodityNameTextView.setText(futureForecast.getName());
            percentageTextView.setText(Double.toString(futureForecast.getPercentChange()).concat("%"));
            if (futureForecast.getPercentChange()>0) {
                Glide.with(context).load(R.drawable.ic_trending_up_black_24dp).into(trendingUpOrDownImageView);
                trendingUpOrDownImageView.setColorFilter(colorGreen, PorterDuff.Mode.SRC_IN);
            } else if (futureForecast.getPercentChange()<0) {
                Glide.with(context).load(R.drawable.ic_trending_down_black_24dp).into(trendingUpOrDownImageView);
                trendingUpOrDownImageView.setColorFilter(colorDanger, PorterDuff.Mode.SRC_IN);
            } else if (futureForecast.getPercentChange()==0) {
                Glide.with(context).load(R.drawable.ic_sentiment_neutral_black_24dp).into(trendingUpOrDownImageView);
                trendingUpOrDownImageView.setColorFilter(colorTextDark, PorterDuff.Mode.SRC_IN);
            }
        }
    }
}
