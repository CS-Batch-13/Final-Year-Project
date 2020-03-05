package abhishekwl.github.io.radar.Fragments;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import abhishekwl.github.io.radar.Adapters.ParametersRecyclerViewAdapter;
import abhishekwl.github.io.radar.Models.Post;
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
public class VerticalsFragment extends android.support.v4.app.Fragment {

    @BindString(R.string.base_server_url)
    String baseServerUrl;
    @BindView(R.id.verticalsFragmentShimmerRecyclerView)
    ShimmerRecyclerView verticalsFragmentShimmerRecyclerView;

    private View rootView;
    private Unbinder unbinder;
    private OkHttpClient okHttpClient;
    private String category;
    private String place;
    private String title;
    private ParametersRecyclerViewAdapter parametersRecyclerViewAdapter;
    private ExtractWordsFromTitle extractWordsFromTitle = new ExtractWordsFromTitle();
    private PerformQuerySearch performQuerySearch = new PerformQuerySearch();

    public VerticalsFragment() {
        // Required empty public constructor
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_verticals, container, false);
        initializeViews();
        initializeComponents();
        return rootView;
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(VerticalsFragment.this, rootView);
    }

    private void initializeComponents() {
        okHttpClient = new OkHttpClient();
        extractWordsFromTitle.execute(title);
    }

    @SuppressLint("StaticFieldLeak")
    private class ExtractWordsFromTitle extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                String requestUrl = "http://analytics.eventregistry.org/api/v1/annotate?apiKey=e2860828-3444-4dde-8cae-0694f2201f34&text="+strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray annotationsJsonArray = rootJson.getJSONArray("annotations");
                ArrayList<String> titlesArrayList = new ArrayList<>();
                for (int i=0; i<annotationsJsonArray.length(); i++) titlesArrayList.add(annotationsJsonArray.getJSONObject(i).getString("title"));
                return titlesArrayList;
            } catch (IOException | JSONException e) {
                Log.v("EX_WORDS_TITLE", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            if (strings==null || strings.isEmpty()) Snackbar.make(verticalsFragmentShimmerRecyclerView, "Couldn't find any data regarding "+category, Snackbar.LENGTH_LONG).show();
            else {
                performQuerySearch.execute(strings.get(0));
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PerformQuerySearch extends AsyncTask<String, Void, ArrayList<Post>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            verticalsFragmentShimmerRecyclerView.showShimmerAdapter();
        }

        @Override
        protected ArrayList<Post> doInBackground(String... strings) {
            try {
                String requestUrl = baseServerUrl + "/hashtags/query?query=" + strings[0] + " "+category+" "+place;
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray resultsJsonArray = rootJson.getJSONArray("results");
                ArrayList<Post> postArrayList = new ArrayList<>();
                for (int i = 0; i < resultsJsonArray.length(); i++) {
                    JSONObject postJson = resultsJsonArray.getJSONObject(i);
                    String title = postJson.getString("title");
                    String description = postJson.getString("summary");
                    String url = postJson.getString("source_url");
                    String sourceName = postJson.getString("source_name");
                    String imageUrl = postJson.getString("image_url");
                    Post post = new Post(title, null, description, description, imageUrl, null, sourceName, url, null);
                    postArrayList.add(post);
                }
                parametersRecyclerViewAdapter = new ParametersRecyclerViewAdapter(postArrayList, category);
                return postArrayList;
            } catch (IOException | JSONException e) {
                Log.v("PERFORM_QUERY_SEARCH", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            super.onPostExecute(posts);
            if (posts == null || posts.isEmpty())
                Snackbar.make(verticalsFragmentShimmerRecyclerView, "Couldn't find anything on #" + category, Snackbar.LENGTH_LONG).show();
            else {
                if (verticalsFragmentShimmerRecyclerView != null) verticalsFragmentShimmerRecyclerView.setAdapter(parametersRecyclerViewAdapter);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(VerticalsFragment.this, rootView);
    }


    @Override
    public void onDestroyView() {
        unbinder.unbind();
        if (extractWordsFromTitle !=null && extractWordsFromTitle.getStatus()== AsyncTask.Status.RUNNING)
            extractWordsFromTitle.cancel(true);
        if (performQuerySearch !=null && performQuerySearch.getStatus()== AsyncTask.Status.RUNNING)
            performQuerySearch.cancel(true);
        super.onDestroyView();
    }

}
