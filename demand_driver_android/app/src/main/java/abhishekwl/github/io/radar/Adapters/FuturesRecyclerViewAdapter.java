package abhishekwl.github.io.radar.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import abhishekwl.github.io.radar.Models.Future;
import abhishekwl.github.io.radar.R;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FuturesRecyclerViewAdapter extends RecyclerView.Adapter<FuturesRecyclerViewAdapter.FuturesViewHolder> {

    private ArrayList<Future> futureArrayList;
    private OkHttpClient okHttpClient;
    private Context context;
    private String baseServerUrl;

    public FuturesRecyclerViewAdapter(ArrayList<Future> futureArrayList, OkHttpClient okHttpClient, Context context) {
        this.futureArrayList = futureArrayList;
        this.okHttpClient = okHttpClient;
        this.context = context;
        this.baseServerUrl = context.getString(R.string.base_server_url);
    }

    @NonNull
    @Override
    public FuturesRecyclerViewAdapter.FuturesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.futures_list_item, viewGroup, false);
        return new FuturesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FuturesRecyclerViewAdapter.FuturesViewHolder futuresViewHolder, int i) {
        Future future = futureArrayList.get(i);
        futuresViewHolder.bind(future, futuresViewHolder.itemView.getContext());
    }

    @Override
    public int getItemCount() {
        return futureArrayList.size();
    }

    class FuturesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.futuresListItemCommodityNameTextView)
        TextView commodityNameTextView;
        @BindView(R.id.futuresListItemLineChartView)
        LineChart lineChart;
        @BindView(R.id.futuresListItemProgressBar)
        ProgressBar progressBar;
        @BindColor(R.color.colorAccent)
        int colorAccent;
        @BindColor(R.color.colorTextDark)
        int colorTextDark;
        @BindColor(R.color.colorDanger)
        int colorDanger;
        @BindColor(R.color.colorPrimaryDark)
        int colorPrimaryDark;
        @BindColor(R.color.colorGridBackground)
        int colorGridBackground;
        @BindColor(R.color.colorGreen)
        int colorGreen;

        FuturesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Future future, Context context) {
            commodityNameTextView.setText(future.getTitle());
            new FetchFutureForecasts().execute(future.getCode());
        }

        @SuppressLint("StaticFieldLeak")
        private class FetchFutureForecasts extends AsyncTask<String, Void, LineData> {

            ArrayList<Entry> entryArrayListLastTradePrice = new ArrayList<>();
            ArrayList<Entry> entryArrayListLowPrices = new ArrayList<>();
            ArrayList<Entry> entryArrayListHighPrices = new ArrayList<>();

            String getMonthForInt(int num) {
                if (num>12) num=0;
                String month = "NEXT YEAR";
                DateFormatSymbols dfs = new DateFormatSymbols();
                String[] months = dfs.getMonths();
                if (num >= 0 && num <= 11 ) {
                    month = months[num];
                }
                return month.substring(0,3);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Description description = new Description();
                description.setText("");
                lineChart.getAxisRight().setDrawLabels(false);
                lineChart.getAxisLeft().setGridColor(colorGridBackground);
                lineChart.getXAxis().setGridColor(colorGridBackground);
                lineChart.getAxisRight().setGridColor(colorGridBackground);
                lineChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
                lineChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
                lineChart.setAutoScaleMinMaxEnabled(true);
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.setDescription(description);
                lineChart.getXAxis().setValueFormatter((value, axis) -> getMonthForInt((int) value));
                progressBar.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.GONE);
            }

            @Override
            protected LineData doInBackground(String... strings) {
                try {
                    String requestUrl = baseServerUrl+"/commodities/futures/forecasts?query="+strings[0].toUpperCase();
                    Request request = new Request.Builder().url(requestUrl).build();
                    Response response = okHttpClient.newCall(request).execute();
                    JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());

                    if (rootJson.get("results")!=null) {
                        JSONArray resultsJsonArray = rootJson.getJSONArray("results");
                        for (int i=0; i<resultsJsonArray.length(); i++) {
                            JSONObject resultJson = resultsJsonArray.getJSONObject(i);
                            double high = resultJson.getDouble("high");
                            double low = resultJson.getDouble("low");
                            double lastPrice = resultJson.getDouble("lastPrice");
                            double percentChange = resultJson.getDouble("percentChange");
                            String symbol = resultJson.getString("symbol");
                            entryArrayListLastTradePrice.add(new Entry(i+1, (float)(lastPrice)));
                            entryArrayListLowPrices.add(new Entry(i+1, (float)(low)));
                            entryArrayListHighPrices.add(new Entry(i+1, (float)(high)));
                        }
                    }
                    LineDataSet lineDataSetLastTradedPrice = new LineDataSet(entryArrayListLastTradePrice, "Last Traded Price");
                    lineDataSetLastTradedPrice.setColor(colorTextDark);
                    lineDataSetLastTradedPrice.setValueTextColor(colorTextDark);

                    LineDataSet lineDataSetLowPrices = new LineDataSet(entryArrayListLowPrices, "Low");
                    lineDataSetLowPrices.setColor(colorDanger);
                    lineDataSetLowPrices.setValueTextColor(colorTextDark);

                    LineDataSet lineDataSetHighPrices = new LineDataSet(entryArrayListHighPrices, "High");
                    lineDataSetHighPrices.setColor(colorGreen);
                    lineDataSetHighPrices.setValueTextColor(colorTextDark);

                    List<ILineDataSet> iLineDataSetList = new ArrayList<>();
                    iLineDataSetList.add(lineDataSetLastTradedPrice);
                    iLineDataSetList.add(lineDataSetLowPrices);
                    iLineDataSetList.add(lineDataSetHighPrices);

                    return new LineData(iLineDataSetList);
                } catch (IOException | JSONException e) {
                    Log.v("FETCH_FUTURE_FORECASTS", e.getMessage()+"\t"+strings[0]);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(LineData lineData) {
                super.onPostExecute(lineData);
                if (lineData!=null) {
                    progressBar.setVisibility(View.GONE);
                    lineChart.setVisibility(View.VISIBLE);
                    lineChart.animateXY(750,750);
                    lineChart.setData(lineData);
                    lineChart.invalidate();
                }
            }
        }

    }
}
