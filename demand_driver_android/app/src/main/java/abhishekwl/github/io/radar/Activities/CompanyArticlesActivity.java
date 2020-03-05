package abhishekwl.github.io.radar.Activities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import abhishekwl.github.io.radar.Adapters.ParametersRecyclerViewAdapter;
import abhishekwl.github.io.radar.Models.Company;
import abhishekwl.github.io.radar.Models.Post;
import abhishekwl.github.io.radar.R;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CompanyArticlesActivity extends AppCompatActivity {

    @BindView(R.id.companyArticlesRecyclerView)
    ShimmerRecyclerView companyArticlesRecyclerView;
    @BindString(R.string.base_server_url)
    String baseServerUrl;

    private Unbinder unbinder;
    private OkHttpClient okHttpClient;
    private Company selectedCompany;
    private ParametersRecyclerViewAdapter parametersRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_articles);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(CompanyArticlesActivity.this);
        companyArticlesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        companyArticlesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        companyArticlesRecyclerView.setHasFixedSize(true);
    }

    private void initializeComponents() {
        okHttpClient = new OkHttpClient();
        selectedCompany = getIntent().getParcelableExtra("COMPANY");
        Objects.requireNonNull(getSupportActionBar()).setTitle(selectedCompany.getName());
        new FetchCompanyArticles().execute(selectedCompany.getId());
    }


    @SuppressLint("StaticFieldLeak")
    private class FetchCompanyArticles extends AsyncTask<Integer, Void, ArrayList<Post>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            companyArticlesRecyclerView.showShimmerAdapter();
        }

        @Override
        protected ArrayList<Post> doInBackground(Integer... integers) {
            try {
                String requestUrl = baseServerUrl+"/companies/insights?company_id="+integers[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray resultsJsonArray = rootJson.getJSONArray("results");
                ArrayList<Post> postArrayList = new ArrayList<>();
                for (int i=0; i<resultsJsonArray.length(); i++) {
                    JSONObject articleJson = resultsJsonArray.getJSONObject(i);
                    String title = articleJson.getString("title");
                    String description = articleJson.getString("summary");
                    String sourceName = articleJson.getString("source_name");
                    String url = articleJson.getString("source_url");
                    String articleDate = articleJson.getString("pub_date");
                    String image = articleJson.getString("image_url");
                    if (TextUtils.isEmpty(image) || image.equalsIgnoreCase("null") || image.equalsIgnoreCase("none")) image="http://sylvie-corbelin.com/wp-content/uploads/2015/02/import_placeholder.png";
                    Post post = new Post(title, null, description, description, image, null, sourceName, url, null);
                    postArrayList.add(post);
                }
                parametersRecyclerViewAdapter = new ParametersRecyclerViewAdapter(postArrayList, null);
                return postArrayList;
            } catch (IOException | JSONException e) {
                Log.v("FETCH_COMPANY_ARTICLES", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Post> postArrayList) {
            super.onPostExecute(postArrayList);
            if (postArrayList==null || postArrayList.isEmpty()) Snackbar.make(companyArticlesRecyclerView, "Error fetching insights", Snackbar.LENGTH_LONG).show();
            else {
                companyArticlesRecyclerView.setAdapter(parametersRecyclerViewAdapter);
            }
        }
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

}
