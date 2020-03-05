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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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

import abhishekwl.github.io.radar.Activities.MainActivity;
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
public class CommodityParameterFragment extends Fragment {

    @BindView(R.id.commodityParameterFragmentShimmerRecyclerView)
    ShimmerRecyclerView commodityFragmentShimmerRecyclerView;
    @BindString(R.string.base_server_url)
    String baseServerUrl;
    @BindView(R.id.commodityParameterChipGroup)
    ChipGroup commodityParameterChipGroup;
    @BindColor(R.color.colorAccent)
    int colorAccent;
    @BindColor(R.color.colorPrimaryDark)
    int colorPrimaryDark;

    private String parameterName;
    private String parameterTitle;
    private Unbinder unbinder;
    private View rootView;
    private OkHttpClient okHttpClient;
    private ParametersRecyclerViewAdapter parametersRecyclerViewAdapter;
    private FetchPosts fetchPosts = new FetchPosts();
    private FetchParametersFromParameterGroup fetchParametersFromParameterGroup = new FetchParametersFromParameterGroup();

    public CommodityParameterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_commodity_parameter, container, false);
        initializeViews();
        initializeComponents();
        return rootView;
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(CommodityParameterFragment.this, rootView);
        commodityFragmentShimmerRecyclerView.showShimmerAdapter();
        commodityFragmentShimmerRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        commodityFragmentShimmerRecyclerView.setHasFixedSize(true);
        commodityFragmentShimmerRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initializeComponents() {
        okHttpClient = ((MainActivity) Objects.requireNonNull(getActivity())).getOkHttpClient();
        fetchParametersFromParameterGroup.execute(parameterName.replace(" ","_"));
        fetchPosts.execute(parameterName);
    }

    public void setParameterTitle(String parameterTitle) {
        this.parameterTitle = parameterTitle;
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
                if (commodityParameterChipGroup!=null)
                    Snackbar.make(commodityParameterChipGroup, "No parameters found for "+parameterName, Snackbar.LENGTH_LONG)
                    .setAction("RETRY", v -> fetchParametersFromParameterGroup.execute(parameterName)).setActionTextColor(colorAccent)
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
                        startActivity(queryHashtagIntent);
                    });
                    chip.setOnLongClickListener(v -> {
                        Snackbar.make(commodityParameterChipGroup, title, Snackbar.LENGTH_SHORT).show();
                        return true;
                    });
                    if (commodityParameterChipGroup!=null) commodityParameterChipGroup.addView(chip);
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchPosts extends AsyncTask<String, Void, ArrayList<Post>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            commodityFragmentShimmerRecyclerView.showShimmerAdapter();
        }

        @Override
        protected ArrayList<Post> doInBackground(String... strings) {
            try {
                ArrayList<Post> posts = new ArrayList<>();
                String requestUrl = baseServerUrl + "/query/india?query="+strings[0];
                if (parameterTitle!=null) requestUrl = requestUrl+" "+parameterTitle;
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootPostsJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONObject articlesRootJson = rootPostsJson.getJSONObject("articles");
                JSONArray resultsJsonArray = articlesRootJson.getJSONArray("results");
                for (int i=0; i<resultsJsonArray.length(); i++) {
                    JSONObject resultJson = resultsJsonArray.getJSONObject(i);
                    JSONObject locationJson = resultJson.getJSONObject("location");
                    JSONObject labelJson = locationJson.getJSONObject("label");
                    String placeName = labelJson.getString("eng");
                    String title = resultJson.getString("title");
                    String author = resultJson.getJSONObject("source").getString("title");
                    String description = resultJson.getString("body");
                    String imageUrl = resultJson.getString("image");
                    String articleUrl = resultJson.getString("url");
                    Post post = new Post(title, author, description, description, imageUrl, placeName, author, articleUrl, null);
                    posts.add(post);
                }
                parametersRecyclerViewAdapter = new ParametersRecyclerViewAdapter(posts, parameterName);
                return posts;
            } catch (IOException | JSONException e) {
                Log.v("FETCH_POSTS", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Post> postArrayList) {
            super.onPostExecute(postArrayList);
            if (postArrayList == null || postArrayList.isEmpty())
                Snackbar.make(commodityFragmentShimmerRecyclerView, "Error fetching data from server", Snackbar.LENGTH_LONG).show();
            else {
                if (commodityFragmentShimmerRecyclerView != null)
                    commodityFragmentShimmerRecyclerView.setAdapter(parametersRecyclerViewAdapter);
            }
        }
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(CommodityParameterFragment.this, rootView);
    }

    @Override
    public void onStop() {
        if (fetchPosts !=null && fetchPosts.getStatus()== AsyncTask.Status.RUNNING)
            fetchPosts.cancel(true);
        if (fetchParametersFromParameterGroup !=null && fetchParametersFromParameterGroup.getStatus()== AsyncTask.Status.RUNNING)
            fetchParametersFromParameterGroup.cancel(true);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        if (fetchPosts !=null && fetchPosts.getStatus()== AsyncTask.Status.RUNNING)
            fetchPosts.cancel(true);
        if (fetchParametersFromParameterGroup !=null && fetchParametersFromParameterGroup.getStatus()== AsyncTask.Status.RUNNING)
            fetchParametersFromParameterGroup.cancel(true);
        super.onDestroyView();
    }
}
