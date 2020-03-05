package abhishekwl.github.io.radar.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.card.MaterialCardView;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import abhishekwl.github.io.radar.Adapters.AffectedCommoditiesRecyclerViewAdapter;
import abhishekwl.github.io.radar.Adapters.AffectedGovernmentPolicyRecyclerViewAdapter;
import abhishekwl.github.io.radar.Adapters.PatentsRecyclerViewAdapter;
import abhishekwl.github.io.radar.Adapters.TweetsRecyclerViewAdapter;
import abhishekwl.github.io.radar.Models.FutureForecast;
import abhishekwl.github.io.radar.Models.Post;
import abhishekwl.github.io.radar.R;
import butterknife.BindColor;
import butterknife.BindFont;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AnalyzePostActivity extends AppCompatActivity {

    @BindView(R.id.analyzePostTitleTextView)
    TextView postListItemTitleTextView;
    @BindView(R.id.analyzePostDescriptionTextView)
    TextView postListItemDescriptionTextView;
    @BindView(R.id.analyzePostSourceNameTextView)
    TextView postListItemSourceNameTextView;
    @BindView(R.id.analyzePostShareImageView)
    ImageView postListItemShareImageView;
    @BindString(R.string.base_server_url)
    String baseServerUrl;
    @BindView(R.id.analyzePostAffectedCommoditiesRecyclerView)
    RecyclerView analyzePostAffectedCommoditiesRecyclerView;
    @BindView(R.id.analyzePostVisitWebsiteImageView)
    ImageView analyzePostVisitWebsiteImageView;
    @BindView(R.id.analyzePostSentimentAnalysisPolarityPieChart)
    PieChart analyzePostSentimentAnalysisPolarityPieChart;
    @BindView(R.id.analyzePostSentimentAnalysisSubjectivityPieChart)
    PieChart analyzePostSentimentAnalysisSubjectivityPieChart;
    @BindView(R.id.analyzePostSentimentAnalysisProgressBar)
    ProgressBar analyzePostSentimentAnalysisProgressBar;
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
    @BindView(R.id.analyzePostSummaryTextView)
    TextView analyzePostSummaryTextView;
    @BindView(R.id.analyzePostDescriptionDropDownImageView)
    ImageView analyzePostDescriptionDropDownImageView;
    @BindFont(R.font.helvetica_bold)
    Typeface helveticaTypeface;
    @BindView(R.id.analyzePostDeducedParametersChipGroup)
    ChipGroup analyzePostDeducedParametersChipGroup;
    @BindView(R.id.analyzePostDeducedParametersProgressBar)
    ProgressBar analyzePostDeducedParametersProgressBar;
    @BindString(R.string.google_trends_api)
    String baseGoogleTrendsServerUrl;
    @BindView(R.id.analyzePostRelatedGovernmentPoliciesRecyclerView)
    RecyclerView analyzePostRelatedGovernmentPoliciesRecyclerView;
    @BindView(R.id.analyzePostGovernmentPoliciesProgressBar)
    ProgressBar analyzePostGovernmentPoliciesProgressBar;
    @BindView(R.id.analyzePostInvestmentTextView)
    TextView analyzePostInvestmentTextView;
    @BindView(R.id.analyzePostGovernmentInvestmentProgressBar)
    ProgressBar analyzePostGovernmentInvestmentProgressBar;
    @BindView(R.id.analyzePostInvestmentCardView)
    MaterialCardView analyzePostInvestmentCardView;
    @BindView(R.id.analyzePostVerticalsFAB)
    FloatingActionButton analyzePostVerticalsFAB;
    @BindView(R.id.analyzePostDeducedParametersScoreTextView)
    TextView analyzePostDeducedParametersScoreTextView;
    @BindView(R.id.analyzePostSentimentAnalysisScoreTextView)
    TextView analyzePostSentimentAnalysisScoreTextView;
    @BindView(R.id.analyzePostGovernmentInvestmentScoreTextView)
    TextView analyzePostGovernmentInvestmentScoreTextView;
    @BindView(R.id.analyzePostGovernmentPolicyScoreTextView)
    TextView analyzePostGovernmentPolicyScoreTextView;
    @BindView(R.id.analyzePostFinalScoreTextView)
    TextView analyzePostFinalScoreTextView;

    private MaterialDialog materialDialog;
    private Unbinder unbinder;
    private OkHttpClient okHttpClient;
    private Post selectedPost;
    private TweetsRecyclerViewAdapter tweetsRecyclerViewAdapter;
    private PatentsRecyclerViewAdapter patentsRecyclerViewAdapter;
    private AffectedCommoditiesRecyclerViewAdapter affectedCommoditiesRecyclerViewAdapter;
    private AffectedGovernmentPolicyRecyclerViewAdapter affectedGovernmentPolicyRecyclerViewAdapter;
    private String categoryName;

    private FetchPostDescription fetchPostDescription = new FetchPostDescription();
    private FetchSentimentAnalysis fetchSentimentAnalysis = new FetchSentimentAnalysis();
    private FetchSummaryFromDescription fetchSummaryFromDescription = new FetchSummaryFromDescription();
    private FetchDeducedParametersFromUrl fetchDeducedParametersFromUrl = new FetchDeducedParametersFromUrl();
    private AddHashTag addHashTag = new AddHashTag();
    private FetchRelatedGovernmentPolicies fetchRelatedGovernmentPolicies = new FetchRelatedGovernmentPolicies();
    private FetchInvestmentsAndGovernmentPolicies fetchInvestmentsAndGovernmentPolicies = new FetchInvestmentsAndGovernmentPolicies();
    private FetchTrendsByRegion fetchTrendsByRegion = new FetchTrendsByRegion();

    int flag = 0;
    double finalScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_analyze_post);

        initializeViews();
        initializeComponents();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeViews() {
        unbinder = ButterKnife.bind(AnalyzePostActivity.this);
        selectedPost = getIntent().getParcelableExtra("POST");
        categoryName = getIntent().getStringExtra("CATEGORY");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#f44336\"><b>" + selectedPost.getTitle() + "</b></font>", Html.FROM_HTML_MODE_COMPACT));
        } else
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#f44336\"><b>" + selectedPost.getTitle() + "</b></font>"));
        renderPost(selectedPost);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .callTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();

        analyzePostRelatedGovernmentPoliciesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        analyzePostRelatedGovernmentPoliciesRecyclerView.setHasFixedSize(true);
        analyzePostRelatedGovernmentPoliciesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        analyzePostAffectedCommoditiesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        analyzePostAffectedCommoditiesRecyclerView.setHasFixedSize(true);
        analyzePostAffectedCommoditiesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (selectedPost.getDescription().contains("http")) {
            fetchPostDescription.execute(selectedPost.getDescription());
            postListItemDescriptionTextView.setText("Crawling and scraping content...");
            analyzePostSummaryTextView.setText("Crawling and scraping content...");
        } else {
            analyzePostAffectedCommoditiesRecyclerView.setVisibility(View.GONE);
            analyzePostSummaryTextView.setVisibility(View.GONE);
        }
    }

    private void initializeComponents() {
        fetchTrendsByRegion.execute(selectedPost.getTitle().replace(":", "").split(" ")[3]);
        fetchRelatedGovernmentPolicies.execute(selectedPost.getTitle());
        fetchInvestmentsAndGovernmentPolicies.execute(categoryName);
        fetchSentimentAnalysis.execute(selectedPost.getTitle());
        fetchDeducedParametersFromUrl.execute(selectedPost.getDescription());
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchTrendsByRegion extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String requestUrl = baseGoogleTrendsServerUrl + "/interest_by_region?query=" + strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONObject defaultJson = rootJson.getJSONObject("default");
                JSONArray geoMapJsonArray = defaultJson.getJSONArray("geoMapData");
                for (int i = 0; i < geoMapJsonArray.length(); i++) {
                    JSONObject geoJson = geoMapJsonArray.getJSONObject(i);
                    String geoName = geoJson.getString("geoName").toLowerCase();
                    String formattedValue = geoJson.getJSONArray("formattedValue").getString(0);
                    if (selectedPost.getSourceId().toLowerCase().contains(geoName)) {
                        return formattedValue;
                    }
                }
                return null;
            } catch (IOException | JSONException e) {
                Log.v("REGION", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.isEmpty()) {
                Snackbar.make(postListItemTitleTextView, "Searches for this news article from " + selectedPost.getSourceId() + " is " + s, Snackbar.LENGTH_LONG).show();
                finalScore+=Integer.parseInt(s);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchInvestmentsAndGovernmentPolicies extends AsyncTask<String, Void, String> {

        double investmentScore = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            analyzePostGovernmentInvestmentProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String requestUrl = baseServerUrl + "/related/ibef?query=" + strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONObject dataJson = rootJson.getJSONObject("data");
                if (dataJson == null) return null;
                else {
                    String source_url = dataJson.getString("url");
                    requestUrl = "http://analytics.eventregistry.org/api/v1/extractArticleInfo?apiKey=e2860828-3444-4dde-8cae-0694f2201f34&url=" + source_url;
                    request = new Request.Builder().url(requestUrl).build();
                    response = okHttpClient.newCall(request).execute();
                    rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                    return rootJson.getString("body");
                }
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            investmentScore = countChar(s, '%');
            investmentScore = investmentScore+5.2;
            analyzePostGovernmentInvestmentScoreTextView.setText(Double.toString(Math.round(investmentScore * 100.0) / 100.0));
            finalScore += investmentScore;
            flag+=1;
            if (flag==4) analyzePostFinalScoreTextView.setText(Double.toString(Math.round(finalScore/flag * 100.0) / 100.0));
            analyzePostGovernmentInvestmentProgressBar.setVisibility(View.GONE);
            if (s == null || s.isEmpty()) analyzePostInvestmentCardView.setVisibility(View.GONE);
            else analyzePostInvestmentTextView.setText(s);
        }

        int countChar(String str, char c) {
            int count = 0;

            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == c)
                    count++;
            }

            return count;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchRelatedGovernmentPolicies extends AsyncTask<String, Void, ArrayList<String>> {

        double govtPolicyScore = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            analyzePostGovernmentPoliciesProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                ArrayList<String> policiesArrayList = new ArrayList<>();
                String requestUrl = baseServerUrl + "/extract/related/article/policies?query=" + strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray dataJsonArray = rootJson.getJSONArray("data");
                for (int i = 0; i < 2; i++)
                    policiesArrayList.add(dataJsonArray.getJSONObject(i).getString("summary"));

                return policiesArrayList;
            } catch (IOException | JSONException e) {
                Log.v("GOVT_POLICY", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            if (strings == null || strings.isEmpty())
                Snackbar.make(postListItemTitleTextView, "Government policies not found related to this article", Snackbar.LENGTH_LONG).show();
            else {
                for (int i = 0; i < strings.get(0).length() && i < strings.get(1).length(); i++) {
                    if (strings.get(0).charAt(i) == strings.get(1).charAt(i)) {
                        govtPolicyScore++;
                    }
                }
                govtPolicyScore = govtPolicyScore / strings.get(0).length();
                govtPolicyScore *= 100;
                finalScore += govtPolicyScore;
                flag+=1;
                if (flag==4) analyzePostFinalScoreTextView.setText(Double.toString(Math.round(finalScore/flag * 100.0) / 100.0));
                if (analyzePostGovernmentPoliciesProgressBar != null) {
                    analyzePostGovernmentPoliciesProgressBar.setVisibility(View.GONE);
                    affectedGovernmentPolicyRecyclerViewAdapter = new AffectedGovernmentPolicyRecyclerViewAdapter(strings);
                    analyzePostRelatedGovernmentPoliciesRecyclerView.setAdapter(affectedGovernmentPolicyRecyclerViewAdapter);
                }
                analyzePostGovernmentPolicyScoreTextView.setText(Double.toString(Math.round(govtPolicyScore * 100.0) / 100.0));
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchPostDescription extends AsyncTask<String, Void, Void> {

        private ArrayList<String> codesArrayList = new ArrayList<>();
        private ArrayList<FutureForecast> futureForecastArrayList = new ArrayList<>();

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String requestUrl = baseServerUrl + "/commodities/description?query=" + strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray codesJsonArray = rootJson.getJSONArray("codes");
                codesArrayList.clear();
                for (int i = 0; i < codesJsonArray.length(); i++)
                    codesArrayList.add(codesJsonArray.getString(i));
                String description = rootJson.getString("description");
                ArrayList<String> linksArrayList = new ArrayList<>();
                if (rootJson.has("links")) {
                    JSONArray linksJsonArray = rootJson.getJSONArray("links");
                    for (int i = 0; i < linksJsonArray.length(); i++)
                        linksArrayList.add(linksJsonArray.getString(i));
                }
                selectedPost.setLinksArrayList(linksArrayList);
                selectedPost.setDescription(description);

                futureForecastArrayList.clear();
                for (String code : codesArrayList) {
                    Log.v("CODE", code);
                    requestUrl = baseServerUrl + "/commodities/futures/forecasts?query=" + code;
                    request = new Request.Builder().url(requestUrl).build();
                    response = okHttpClient.newCall(request).execute();
                    rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                    JSONArray resultsJsonArray = rootJson.getJSONArray("results");
                    JSONObject currentMonthFuturesForecast = resultsJsonArray.getJSONObject(0);
                    double low = currentMonthFuturesForecast.getDouble("low");
                    double high = currentMonthFuturesForecast.getDouble("high");
                    double lastPrice = currentMonthFuturesForecast.getDouble("lastPrice");
                    double percentageChange = currentMonthFuturesForecast.getDouble("percentChange");
                    String commodityName = currentMonthFuturesForecast.getString("name");
                    String symbol = currentMonthFuturesForecast.getString("symbol");
                    FutureForecast futureForecast = new FutureForecast(high, low, lastPrice, percentageChange, symbol, commodityName);
                    futureForecastArrayList.add(futureForecast);
                }
                affectedCommoditiesRecyclerViewAdapter = new AffectedCommoditiesRecyclerViewAdapter(futureForecastArrayList, getApplicationContext());
            } catch (IOException | JSONException e) {
                Log.v("FETCH_POST_DESC", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (selectedPost.getDescription().contains("<div>") || selectedPost.getDescription().contains("<pre>") || selectedPost.getDescription().contains("<p>")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    postListItemDescriptionTextView.setText(Html.fromHtml(selectedPost.getDescription(), Html.FROM_HTML_MODE_COMPACT));
                } else
                    postListItemDescriptionTextView.setText(Html.fromHtml(selectedPost.getDescription()));
                analyzePostAffectedCommoditiesRecyclerView.setAdapter(affectedCommoditiesRecyclerViewAdapter);
                fetchSummaryFromDescription.execute(selectedPost.getDescription());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchDeducedParametersFromUrl extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            analyzePostDeducedParametersProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                String requestUrl = "http://analytics.eventregistry.org/api/v1/annotate?apiKey=e2860828-3444-4dde-8cae-0694f2201f34&text=" + strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                ArrayList<String> nounsArrayList = new ArrayList<>();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray annotationsJsonArray = rootJson.getJSONArray("annotations");
                for (int i = 0; i < annotationsJsonArray.length(); i++) {
                    JSONObject annotationObject = annotationsJsonArray.getJSONObject(i);
                    nounsArrayList.add(annotationObject.getString("title"));
                }
                return nounsArrayList;
            } catch (IOException | JSONException e) {
                Log.v("FETCH_DEDUCED", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            double deducedParametersScore = 0;
            for (String noun : strings) {
                if (selectedPost.getTitle().toLowerCase().contains(noun.toLowerCase()))
                    deducedParametersScore += 1;
                String nounName = noun.trim().replace(" ", "_");
                Chip chip = new Chip(AnalyzePostActivity.this);
                chip.setText("#".concat(nounName));
                chip.setTextColor(colorPrimaryDark);
                chip.setTextSize(12f);
                chip.setChipBackgroundColor(getApplicationContext().getColorStateList(R.color.colorAccent));
                chip.setTextColor(colorPrimaryDark);
                chip.setOnClickListener(v -> {
                    materialDialog = new MaterialDialog.Builder(AnalyzePostActivity.this)
                            .title(R.string.app_name)
                            .content("Do you want to track " + noun + "?")
                            .titleColorRes(android.R.color.black)
                            .contentColorRes(R.color.colorTextDark)
                            .positiveText("YES")
                            .negativeText("CANCEL")
                            .positiveColorRes(R.color.colorGreen)
                            .negativeColorRes(R.color.colorDanger)
                            .onPositive((dialog, which) -> addHashTag.execute(noun))
                            .show();
                });
                chip.setOnLongClickListener(v -> {
                    Snackbar.make(analyzePostDeducedParametersChipGroup, noun, Snackbar.LENGTH_SHORT).show();
                    return true;
                });
                if (analyzePostDeducedParametersChipGroup != null) {
                    analyzePostDeducedParametersChipGroup.addView(chip);
                    analyzePostDeducedParametersProgressBar.setVisibility(View.GONE);
                }
            }
            deducedParametersScore = (deducedParametersScore / strings.size()) * 100;
            finalScore += deducedParametersScore;
            flag+=1;
            if (flag==4) analyzePostFinalScoreTextView.setText(Double.toString(Math.round(finalScore/flag * 100.0) / 100.0));
            analyzePostDeducedParametersScoreTextView.setText(Double.toString(Math.round(deducedParametersScore * 100.0) / 100.0));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AddHashTag extends AsyncTask<String, Void, Void> {

        private boolean success;
        private String hashTag;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                hashTag = strings[0];
                String title = hashTag + " " + categoryName;
                categoryName = categoryName.toLowerCase();
                String requestUrl = baseServerUrl + "/parameters/hashtag/add?display=" + hashTag + "&title=" + title + "&category=" + categoryName;
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                success = response.isSuccessful();
            } catch (IOException e) {
                Log.v("ADD_HASH", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (success)
                Snackbar.make(analyzePostDeducedParametersChipGroup, "Added new paramter " + hashTag, Snackbar.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchSummaryFromDescription extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            analyzePostSummaryTextView.setText("Attempting content summarization..");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String requestUrl = baseServerUrl + "/summary?query=" + strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                return rootJson.getString("text").replace("\r", "\n").replace("\n", "\n\n");
            } catch (IOException | JSONException e) {
                Log.v("FETCH_SUMMARY", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                analyzePostSummaryTextView.setText("");
                analyzePostSummaryTextView.setVisibility(View.GONE);
            } else {
                analyzePostSummaryTextView.setText(s);
                postListItemDescriptionTextView.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.analyzePostVerticalsFAB)
    public void onVerticalsFABPress() {
        Intent verticalsIntent = new Intent(AnalyzePostActivity.this, VerticalsActivity.class);
        verticalsIntent.putExtra("POST", selectedPost);
        startActivity(verticalsIntent);
    }

    private void renderPost(Post selectedPost) {
        postListItemTitleTextView.setText(selectedPost.getTitle());
        postListItemDescriptionTextView.setText(selectedPost.getDescription().replace("\n", " "));
        postListItemSourceNameTextView.setText(selectedPost.getSourceName());
        postListItemShareImageView.setOnClickListener(v -> {
            String shareContent = selectedPost.getTitle() + "\n" + selectedPost.getSourceName() + "\n\n\n" + selectedPost.getDescription() + "\n\n" + selectedPost.getUrl();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
            startActivity(Intent.createChooser(shareIntent, "Share Post"));
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchSentimentAnalysis extends AsyncTask<String, Void, Void> {

        PieData pieDataPolarity, pieDataSubjectivity;

        double sentimentScore = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Description description = new Description();
            description.setText("");
            analyzePostSentimentAnalysisPolarityPieChart.setDrawEntryLabels(false);
            analyzePostSentimentAnalysisPolarityPieChart.setCenterTextSize(18);
            analyzePostSentimentAnalysisPolarityPieChart.setEntryLabelColor(Color.WHITE);
            analyzePostSentimentAnalysisPolarityPieChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
            analyzePostSentimentAnalysisPolarityPieChart.getLegend().setWordWrapEnabled(true);
            analyzePostSentimentAnalysisPolarityPieChart.setDescription(description);

            analyzePostSentimentAnalysisSubjectivityPieChart.setDrawEntryLabels(false);
            analyzePostSentimentAnalysisSubjectivityPieChart.setCenterTextSize(15);
            analyzePostSentimentAnalysisSubjectivityPieChart.setEntryLabelColor(Color.WHITE);
            analyzePostSentimentAnalysisSubjectivityPieChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
            analyzePostSentimentAnalysisSubjectivityPieChart.getLegend().setWordWrapEnabled(true);
            analyzePostSentimentAnalysisSubjectivityPieChart.setDescription(description);

            analyzePostSentimentAnalysisProgressBar.setVisibility(View.VISIBLE);
            analyzePostSentimentAnalysisPolarityPieChart.setVisibility(View.GONE);
            analyzePostSentimentAnalysisSubjectivityPieChart.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String requestUrl = baseServerUrl + "/sentiment/tweets?query=" + strings[0];
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                int tweetsParsedCount = rootJson.getInt("count");
                double polarityNegative = rootJson.getDouble("polarity_negative");
                double polarityNeutral = rootJson.getDouble("polarity_neutral");
                double polarityPositive = rootJson.getDouble("polarity_positive");
                double subjectivityNegative = rootJson.getDouble("subjectivity_negative");
                double subjectivityNeutral = rootJson.getDouble("subjectivity_neutral");
                double subjectivityPositive = rootJson.getDouble("subjectivity_positive");

                double polarityNegativePercentage = (polarityNegative / tweetsParsedCount) * 100;
                double polarityNeutralPercentage = (polarityNeutral / tweetsParsedCount) * 100;
                double polarityPositivePercentage = (polarityPositive / tweetsParsedCount) * 100;

                double subjectivityNegativePercentage = (subjectivityNegative / tweetsParsedCount) * 100;
                double subjectivityNeutralPercentage = (subjectivityNeutral / tweetsParsedCount) * 100;
                double subjectivityPositivePercentage = (subjectivityPositive / tweetsParsedCount) * 100;

                sentimentScore = (polarityNegativePercentage + subjectivityNeutralPercentage) / 2;
                finalScore += sentimentScore;
                flag+=1;
                if (flag==4) analyzePostFinalScoreTextView.setText(Double.toString(Math.round(finalScore/flag * 100.0) / 100.0));

                ArrayList<PieEntry> pieEntryArrayListPolarity = new ArrayList<>();
                pieEntryArrayListPolarity.add(new PieEntry((float) polarityNegativePercentage, "Percentage of People who have a negative opinion"));
                pieEntryArrayListPolarity.add(new PieEntry((float) polarityPositivePercentage, "Percentage of People who have a positive opinion"));
                pieEntryArrayListPolarity.add(new PieEntry((float) polarityNeutralPercentage, "Percentage of People who have an objective opinion"));
                PieDataSet pieDataSetPolarity = new PieDataSet(pieEntryArrayListPolarity, "");
                pieDataSetPolarity.setColors(colorDanger, colorGreen, colorTextDark);
                pieDataPolarity = new PieData(pieDataSetPolarity);

                ArrayList<PieEntry> pieEntryArrayListSubjectivity = new ArrayList<>();
                pieEntryArrayListSubjectivity.add(new PieEntry((float) subjectivityPositivePercentage, "Percentage of People who are biased in their opinion"));
                pieEntryArrayListSubjectivity.add(new PieEntry((float) subjectivityNegativePercentage, "Percentage of People who are nearly objective in their opinion"));
                pieEntryArrayListSubjectivity.add(new PieEntry((float) subjectivityNeutralPercentage, "Percentage of People who are slightly subjective in their opinion"));
                PieDataSet pieDataSetSubjectivity = new PieDataSet(pieEntryArrayListSubjectivity, "");
                pieDataSetSubjectivity.setColors(colorDanger, colorGreen, colorTextDark);
                pieDataSubjectivity = new PieData(pieDataSetSubjectivity);
            } catch (IOException | JSONException e) {
                Log.v("FETCH_SENTIMENT", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            analyzePostSentimentAnalysisScoreTextView.setText(Double.toString(Math.round(sentimentScore * 100.0) / 100.0));
            analyzePostSentimentAnalysisPolarityPieChart.setCenterText("Polarity");
            analyzePostSentimentAnalysisSubjectivityPieChart.setCenterText("Subjectivity");

            analyzePostSentimentAnalysisProgressBar.setVisibility(View.GONE);
            analyzePostSentimentAnalysisPolarityPieChart.setVisibility(View.VISIBLE);
            analyzePostSentimentAnalysisSubjectivityPieChart.setVisibility(View.VISIBLE);

            analyzePostSentimentAnalysisPolarityPieChart.animateXY(500, 500);
            analyzePostSentimentAnalysisSubjectivityPieChart.animateXY(500, 500);

            analyzePostSentimentAnalysisPolarityPieChart.setData(pieDataPolarity);
            analyzePostSentimentAnalysisPolarityPieChart.invalidate();
            analyzePostSentimentAnalysisSubjectivityPieChart.setData(pieDataSubjectivity);
            analyzePostSentimentAnalysisSubjectivityPieChart.invalidate();

            analyzePostSentimentAnalysisPolarityPieChart.getData().setValueTextColor(Color.WHITE);
            analyzePostSentimentAnalysisPolarityPieChart.getData().setValueTextSize(14f);
            analyzePostSentimentAnalysisSubjectivityPieChart.getData().setValueTextColor(Color.WHITE);
            analyzePostSentimentAnalysisSubjectivityPieChart.getData().setValueTextSize(14f);

            analyzePostSentimentAnalysisPolarityPieChart.setUsePercentValues(true);
            analyzePostSentimentAnalysisSubjectivityPieChart.setUsePercentValues(true);
        }
    }

    @OnClick(R.id.analyzePostDescriptionDropDownImageView)
    public void onPostDescriptionDropDownViewPress() {
        if (postListItemDescriptionTextView.getVisibility() == View.VISIBLE)
            postListItemDescriptionTextView.setVisibility(View.GONE);
        else if (postListItemDescriptionTextView.getVisibility() == View.GONE)
            postListItemDescriptionTextView.setVisibility(View.VISIBLE);
    }

    /*
    @Override
    protected void onStop() {
        if (fetchPostDescription != null && fetchPostDescription.getStatus() == AsyncTask.Status.RUNNING)
            fetchPostDescription.cancel(true);
        if (fetchSentimentAnalysis != null && fetchSentimentAnalysis.getStatus() == AsyncTask.Status.RUNNING)
            fetchSentimentAnalysis.cancel(true);
        if (fetchSummaryFromDescription != null && fetchSummaryFromDescription.getStatus() == AsyncTask.Status.RUNNING)
            fetchSummaryFromDescription.cancel(true);
        if (fetchDeducedParametersFromUrl != null && fetchDeducedParametersFromUrl.getStatus() == AsyncTask.Status.RUNNING)
            fetchDeducedParametersFromUrl.cancel(true);
        if (addHashTag != null && addHashTag.getStatus() == AsyncTask.Status.RUNNING)
            addHashTag.cancel(true);
        if (fetchRelatedGovernmentPolicies != null && fetchRelatedGovernmentPolicies.getStatus() == AsyncTask.Status.RUNNING)
            fetchRelatedGovernmentPolicies.cancel(true);
        if (fetchTrendsByRegion !=null && fetchTrendsByRegion.getStatus()== AsyncTask.Status.RUNNING)
            fetchTrendsByRegion.cancel(true);
        if (fetchInvestmentsAndGovernmentPolicies !=null && fetchInvestmentsAndGovernmentPolicies.getStatus()== AsyncTask.Status.RUNNING)
            fetchInvestmentsAndGovernmentPolicies.cancel(true);
        super.onStop();
    }
    */

    @Override
    public void onBackPressed() {
        if (fetchPostDescription != null && fetchPostDescription.getStatus() == AsyncTask.Status.RUNNING)
            fetchPostDescription.cancel(true);
        if (fetchSentimentAnalysis != null && fetchSentimentAnalysis.getStatus() == AsyncTask.Status.RUNNING)
            fetchSentimentAnalysis.cancel(true);
        if (fetchSummaryFromDescription != null && fetchSummaryFromDescription.getStatus() == AsyncTask.Status.RUNNING)
            fetchSummaryFromDescription.cancel(true);
        if (fetchDeducedParametersFromUrl != null && fetchDeducedParametersFromUrl.getStatus() == AsyncTask.Status.RUNNING)
            fetchDeducedParametersFromUrl.cancel(true);
        if (addHashTag != null && addHashTag.getStatus() == AsyncTask.Status.RUNNING)
            addHashTag.cancel(true);
        if (fetchRelatedGovernmentPolicies != null && fetchRelatedGovernmentPolicies.getStatus() == AsyncTask.Status.RUNNING)
            fetchRelatedGovernmentPolicies.cancel(true);
        if (fetchTrendsByRegion !=null && fetchTrendsByRegion.getStatus()== AsyncTask.Status.RUNNING)
            fetchTrendsByRegion.cancel(true);
        if (fetchInvestmentsAndGovernmentPolicies !=null && fetchInvestmentsAndGovernmentPolicies.getStatus()== AsyncTask.Status.RUNNING)
            fetchInvestmentsAndGovernmentPolicies.cancel(true);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (fetchPostDescription != null && fetchPostDescription.getStatus() == AsyncTask.Status.RUNNING)
            fetchPostDescription.cancel(true);
        if (fetchSentimentAnalysis != null && fetchSentimentAnalysis.getStatus() == AsyncTask.Status.RUNNING)
            fetchSentimentAnalysis.cancel(true);
        if (fetchSummaryFromDescription != null && fetchSummaryFromDescription.getStatus() == AsyncTask.Status.RUNNING)
            fetchSummaryFromDescription.cancel(true);
        if (fetchDeducedParametersFromUrl != null && fetchDeducedParametersFromUrl.getStatus() == AsyncTask.Status.RUNNING)
            fetchDeducedParametersFromUrl.cancel(true);
        if (addHashTag != null && addHashTag.getStatus() == AsyncTask.Status.RUNNING)
            addHashTag.cancel(true);
        if (fetchRelatedGovernmentPolicies != null && fetchRelatedGovernmentPolicies.getStatus() == AsyncTask.Status.RUNNING)
            fetchRelatedGovernmentPolicies.cancel(true);
        if (fetchTrendsByRegion !=null && fetchTrendsByRegion.getStatus()== AsyncTask.Status.RUNNING)
            fetchTrendsByRegion.cancel(true);
        if (fetchInvestmentsAndGovernmentPolicies !=null && fetchInvestmentsAndGovernmentPolicies.getStatus()== AsyncTask.Status.RUNNING)
            fetchInvestmentsAndGovernmentPolicies.cancel(true);
        super.onDestroy();
    }
}
