package abhishekwl.github.io.radar.Activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.airbnb.lottie.LottieAnimationView;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import abhishekwl.github.io.radar.Adapters.CompaniesRecyclerViewAdapter;
import abhishekwl.github.io.radar.Models.Company;
import abhishekwl.github.io.radar.R;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CompanySearchActivity extends AppCompatActivity {

    @BindView(R.id.companySearchRecyclerView)
    ShimmerRecyclerView companySearchRecyclerView;
    @BindString(R.string.base_server_url)
    String baseServerUrl;
    @BindView(R.id.companySearchLottieAnimationView)
    LottieAnimationView companySearchLottieAnimationView;

    private Unbinder unbinder;
    private OkHttpClient okHttpClient;
    private CompaniesRecyclerViewAdapter companiesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_search);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#f44336\"><b>Company Search</b></font>", Html.FROM_HTML_MODE_COMPACT));
        } else Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#f44336\"><b>Company Search</b></font>"));
        unbinder = ButterKnife.bind(CompanySearchActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#f44336\"><b>Search Company</b></font>",Html.FROM_HTML_MODE_COMPACT));
        } else Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#f44336\"><b>Search Company</b></font>"));
        companySearchRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        companySearchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        companySearchRecyclerView.setHasFixedSize(true);
        companySearchRecyclerView.setVisibility(View.GONE);
    }

    private void initializeComponents() {
        okHttpClient = new OkHttpClient();
    }

    @SuppressLint("StaticFieldLeak")
    private class SearchCompanies extends AsyncTask<String, Void, ArrayList<Company>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            companySearchLottieAnimationView.setVisibility(View.GONE);
            companySearchRecyclerView.setVisibility(View.VISIBLE);
            companySearchRecyclerView.showShimmerAdapter();
        }

        @Override
        protected ArrayList<Company> doInBackground(String... strings) {
            try {
                String requestUrl = baseServerUrl + "/companies/search?query=" + strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray resultsJsonArray = rootJson.getJSONArray("results");
                ArrayList<Company> companyArrayList = new ArrayList<>();
                for (int i = 0; i < resultsJsonArray.length(); i++) {
                    JSONObject companyJson = resultsJsonArray.getJSONObject(i);
                    int id = companyJson.getInt("id");
                    String hqState = companyJson.getString("hq_state");
                    String hqCountry = companyJson.getString("hq_country");
                    String name = companyJson.getString("name");
                    String url = companyJson.getString("url");
                    String twitter = companyJson.getString("twitter");
                    String youtube = companyJson.getString("youtube");
                    String linkedIn = companyJson.getString("linkedin");
                    String facebook = companyJson.getString("facebook");
                    String google = companyJson.getString("google");
                    String logo = companyJson.getString("logo");
                    Company company = new Company(id, name, url, twitter, youtube, facebook, google, hqCountry, hqState, linkedIn, logo);
                    companyArrayList.add(company);
                }
                companiesRecyclerViewAdapter = new CompaniesRecyclerViewAdapter(companyArrayList);
                return companyArrayList;
            } catch (IOException | JSONException e) {
                Log.v("SEARCH_COMPANIES", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Company> companies) {
            super.onPostExecute(companies);
            if (companies == null || companies.isEmpty())
                Snackbar.make(companySearchRecyclerView, "Error fetching companies from server", Snackbar.LENGTH_LONG).show();
            else {
                companySearchRecyclerView.setAdapter(companiesRecyclerViewAdapter);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //menu.findItem(R.id.menuItemChart).setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.menuItemSearch);
        searchItem.getIcon().setColorFilter(getColor(R.color.colorTextDark), PorterDuff.Mode.SRC_IN);
        searchItem.getIcon().setColorFilter(getColor(R.color.colorTextDark), PorterDuff.Mode.SRC_IN);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(CompanySearchActivity.this.getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new SearchCompanies().execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            companySearchRecyclerView.setVisibility(View.GONE);
            companySearchLottieAnimationView.setVisibility(View.VISIBLE);
            return true;
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
