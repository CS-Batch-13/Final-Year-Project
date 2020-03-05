package abhishekwl.github.io.radar.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import abhishekwl.github.io.radar.Activities.QueryHashtagActivity;
import abhishekwl.github.io.radar.Adapters.ParametersRecyclerViewAdapter;
import abhishekwl.github.io.radar.Models.Post;
import abhishekwl.github.io.radar.R;
import butterknife.BindColor;
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
public class CommodityFragment extends Fragment {

    @BindView(R.id.commodityParameterFragmentShimmerRecyclerView)
    ShimmerRecyclerView commodityParameterFragmentShimmerRecyclerView;
    @BindString(R.string.base_server_url)
    String baseServerUrl;
    @BindView(R.id.commodityParameterParameterChipGroup)
    ChipGroup commodityParameterParameterChipGroup;
    @BindColor(R.color.colorPrimaryDark)
    int colorPrimaryDark;
    @BindColor(R.color.colorAccent)
    int colorAccent;

    private View rootView;
    private String commodityName;
    private Unbinder unbinder;
    private OkHttpClient okHttpClient;
    private ParametersRecyclerViewAdapter parametersRecyclerViewAdapter;
    private FetchHeadlines fetchHeadlines = new FetchHeadlines();
    private FetchParametersFromParameterGroup fetchParametersFromParameterGroup = new FetchParametersFromParameterGroup();

    public CommodityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_commodity, container, false);
        initializeViews();
        initializeComponents();
        return rootView;
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(CommodityFragment.this, rootView);
    }

    private void initializeComponents() {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(50, TimeUnit.SECONDS)
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .build();
        fetchHeadlines.execute(commodityName);
        fetchParametersFromParameterGroup.execute(commodityName);
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchParametersFromParameterGroup extends AsyncTask<String, Void, HashMap> {

        @Override
        protected HashMap doInBackground(String... strings) {
            try {
                String requestUrl = baseServerUrl + "/commodities/parameters?query=" + strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray hashtagsJsonArray = rootJson.getJSONArray("hashtags");
                HashMap<String,String> hashtagsHashMap = new HashMap<>();
                for (int i = 0; i < hashtagsJsonArray.length(); i++) {
                    JSONObject paramObject = hashtagsJsonArray.getJSONObject(i);
                    String displayName = paramObject.getString("display");
                    String title = paramObject.getString("title");
                    hashtagsHashMap.put(displayName, title);
                }
                return hashtagsHashMap;
            } catch (IOException | JSONException e) {
                Log.v("FETCH_COMMODITIES", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap hashMap) {
            super.onPostExecute(hashMap);
            if (hashMap==null || hashMap.isEmpty()) {
                if (commodityParameterParameterChipGroup!=null)
                    Snackbar.make(commodityParameterParameterChipGroup, "No parameters found for "+commodityName, Snackbar.LENGTH_LONG)
                            .setAction("RETRY", v -> fetchParametersFromParameterGroup.execute(commodityName)).setActionTextColor(colorAccent)
                            .show();
            }
            else {
                for (Object o : hashMap.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    String displayName = pair.getKey().toString();
                    String title = pair.getValue().toString();
                    Chip chip = new Chip(rootView.getContext());
                    chip.setText("#".concat(displayName));
                    chip.setTextColor(colorPrimaryDark);
                    chip.setTextSize(14f);
                    chip.setChipBackgroundColor(rootView.getContext().getColorStateList(R.color.colorAccent));
                    chip.setTextColor(colorPrimaryDark);
                    chip.setOnClickListener(v -> {
                        Intent queryHashtagIntent = new Intent(rootView.getContext(), QueryHashtagActivity.class);
                        queryHashtagIntent.putExtra("HASHTAG_DISPLAY", displayName);
                        queryHashtagIntent.putExtra("HASHTAG_TITLE", title);
                        queryHashtagIntent.putExtra("CATEGORY", commodityName);
                        startActivity(queryHashtagIntent);
                    });
                    chip.setOnLongClickListener(v -> {
                        Snackbar.make(commodityParameterParameterChipGroup, title, Snackbar.LENGTH_SHORT).show();
                        return true;
                    });
                    if (commodityParameterParameterChipGroup!=null) commodityParameterParameterChipGroup.addView(chip);
                }
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class FetchHeadlines extends AsyncTask<String, Void, ArrayList<Post>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            commodityParameterFragmentShimmerRecyclerView.showShimmerAdapter();
        }

        @Override
        protected ArrayList<Post> doInBackground(String... strings) {
            try {
                String requestUrl = baseServerUrl + "/commodities/headlines?query=" + commodityName.trim();
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray articlesJsonArray = rootJsonObject.getJSONArray("articles");
                ArrayList<Post> postArrayList = new ArrayList<>();
                for (int i = 0; i < articlesJsonArray.length(); i++) {
                    JSONObject articleJson = articlesJsonArray.getJSONObject(i);
                    String title = articleJson.getString("title");
                    String url = articleJson.getString("url");
                    Post post = new Post(title, null, url, url, null, null, "futures.tradingcharts", url, null);
                    postArrayList.add(post);
                }
                parametersRecyclerViewAdapter = new ParametersRecyclerViewAdapter(postArrayList, commodityName);
                return postArrayList;
            } catch (IOException | JSONException e) {
                Log.v("ERROR", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            super.onPostExecute(posts);
            if (commodityParameterFragmentShimmerRecyclerView != null)
                commodityParameterFragmentShimmerRecyclerView.setAdapter(parametersRecyclerViewAdapter);
        }
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    @Override
    public void onStop() {
        if (fetchParametersFromParameterGroup!=null && fetchParametersFromParameterGroup.getStatus()== AsyncTask.Status.RUNNING)
            fetchParametersFromParameterGroup.cancel(true);
        if (fetchHeadlines!=null && fetchHeadlines.getStatus()== AsyncTask.Status.RUNNING)
            fetchHeadlines.cancel(true);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        if (fetchParametersFromParameterGroup!=null && fetchParametersFromParameterGroup.getStatus()== AsyncTask.Status.RUNNING)
            fetchParametersFromParameterGroup.cancel(true);
        if (fetchHeadlines!=null && fetchHeadlines.getStatus()== AsyncTask.Status.RUNNING)
            fetchHeadlines.cancel(true);
        super.onDestroyView();
    }
}
