package abhishekwl.github.io.radar.Activities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

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

public class QueryHashtagActivity extends AppCompatActivity {

    @BindView(R.id.queryHashtagShimmerRecyclerView)
    ShimmerRecyclerView queryHashtagShimmerRecyclerView;
    @BindString(R.string.base_server_url)
    String baseServerUrl;

    private Unbinder unbinder;
    private String hashtag_display;
    private String hashtag_title;
    private OkHttpClient okHttpClient;
    private ParametersRecyclerViewAdapter parametersRecyclerViewAdapter;
    private String category;
    private PerformQuerySearch performQuerySearch = new PerformQuerySearch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_query_hashtag);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(QueryHashtagActivity.this);
        category = getIntent().getStringExtra("CATEGORY");
        queryHashtagShimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        queryHashtagShimmerRecyclerView.setHasFixedSize(true);
        queryHashtagShimmerRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initializeComponents() {
        hashtag_display = getIntent().getStringExtra("HASHTAG_DISPLAY");
        hashtag_title = getIntent().getStringExtra("HASHTAG_TITLE");
        hashtag_title = Character.toUpperCase(hashtag_title.charAt(0)) + hashtag_title.substring(1);
        Objects.requireNonNull(getSupportActionBar()).setTitle(hashtag_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#f44336\"><b>"+hashtag_title+"</b></font>",Html.FROM_HTML_MODE_COMPACT));
            getSupportActionBar().setSubtitle(Html.fromHtml("<font color=\"#f44336\"><i>#"+hashtag_display+"</i></font>",Html.FROM_HTML_MODE_COMPACT));
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#f44336\"><b>"+hashtag_title+"</b></font>"));
            getSupportActionBar().setSubtitle(Html.fromHtml("<font color=\"#f44336\"><i>#"+hashtag_display+"</i></font>"));
        }
        okHttpClient = new OkHttpClient();
        performQuerySearch.execute(hashtag_title);
    }

    @SuppressLint("StaticFieldLeak")
    private class PerformQuerySearch extends AsyncTask<String, Void, ArrayList<Post>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            queryHashtagShimmerRecyclerView.showShimmerAdapter();
        }

        @Override
        protected ArrayList<Post> doInBackground(String... strings) {
            try {
                String requestUrl = baseServerUrl+"/hashtags/query?query="+strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray resultsJsonArray = rootJson.getJSONArray("results");
                ArrayList<Post> postArrayList = new ArrayList<>();
                for (int i=0; i<resultsJsonArray.length(); i++) {
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
            if (posts==null || posts.isEmpty()) Snackbar.make(queryHashtagShimmerRecyclerView, "Couldn't find anything on #"+ hashtag_display, Snackbar.LENGTH_LONG).show();
            else {
                if (queryHashtagShimmerRecyclerView!=null) queryHashtagShimmerRecyclerView.setAdapter(parametersRecyclerViewAdapter);
            }
        }
    }

    @Override
    protected void onStop() {
        if (performQuerySearch !=null && performQuerySearch.getStatus()== AsyncTask.Status.RUNNING)
            performQuerySearch.cancel(true);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        if (performQuerySearch !=null && performQuerySearch.getStatus()== AsyncTask.Status.RUNNING)
            performQuerySearch.cancel(true);
        super.onDestroy();
    }
}
