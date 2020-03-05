package abhishekwl.github.io.radar.Fragments;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import abhishekwl.github.io.radar.Activities.FuturesForecastActivity;
import abhishekwl.github.io.radar.Adapters.FuturesRecyclerViewAdapter;
import abhishekwl.github.io.radar.Models.Future;
import abhishekwl.github.io.radar.R;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FuturesFragment extends Fragment {

    @BindView(R.id.futuresFragmentRecyclerView)
    ShimmerRecyclerView futuresFragmentRecyclerView;
    @BindString(R.string.base_server_url)
    String baseServerUrl;

    private View rootView;
    private Unbinder unbinder;
    private OkHttpClient okHttpClient;
    private String commodityGroup;
    private FuturesRecyclerViewAdapter futuresRecyclerViewAdapter;

    public FuturesFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_futures, container, false);
        initializeViews();
        initializeComponents();
        return rootView;
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(FuturesFragment.this, rootView);
    }

    private void initializeComponents() {
        okHttpClient = ((FuturesForecastActivity) Objects.requireNonNull(getActivity())).getOkHttpClient();
        new FetchItemsInCommodityGroupWithCodes().execute(commodityGroup);
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchItemsInCommodityGroupWithCodes extends AsyncTask<String, Void, ArrayList<Future>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            futuresFragmentRecyclerView.showShimmerAdapter();
        }

        @Override
        protected ArrayList<Future> doInBackground(String... strings) {
            try {
                String requestUrl = baseServerUrl+"/commodities/futures/codes?query="+strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray itemsJsonArray = rootJson.getJSONArray("items");
                ArrayList<Future> futureArrayList = new ArrayList<>();
                for (int i=0; i<itemsJsonArray.length(); i++) {
                    JSONObject itemJson = itemsJsonArray.getJSONObject(i);
                    String code = itemJson.getString("code");
                    String title = itemJson.getString("title");
                    futureArrayList.add(new Future(title, code));
                }
                futuresRecyclerViewAdapter = new FuturesRecyclerViewAdapter(futureArrayList, okHttpClient, rootView.getContext());
                return futureArrayList;
            } catch (IOException | JSONException e) {
                Log.v("FETCH_ITEMS_FUTURES", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Future> futures) {
            super.onPostExecute(futures);
            if (futures==null || futures.isEmpty()) Snackbar.make(futuresFragmentRecyclerView, "Error fetching futures codes from server", Snackbar.LENGTH_LONG).show();
            else {
                futuresFragmentRecyclerView.setAdapter(futuresRecyclerViewAdapter);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(FuturesFragment.this, rootView);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    public void setCommodityGroup(String commodityGroup) {
        this.commodityGroup = commodityGroup;
    }
}
