package abhishekwl.github.io.radar.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import abhishekwl.github.io.radar.Adapters.CommoditiesParameterViewPagerAdapter;
import abhishekwl.github.io.radar.Adapters.CommoditiesViewPagerAdapter;
import abhishekwl.github.io.radar.R;
import abhishekwl.github.io.radar.Services.AlertService;
import butterknife.BindArray;
import butterknife.BindColor;
import butterknife.BindFont;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainToolbar)
    Toolbar mainToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindString(R.string.base_server_url)
    String baseServerUrl;
    @BindView(R.id.mainCommodityParametersTabLayout)
    TabLayout mainCommodityParametersTabLayout;
    @BindView(R.id.mainCommodityParametersViewPager)
    ViewPager mainCommodityParametersViewPager;
    @BindView(R.id.mainProgressBar)
    ProgressBar mainProgressBar;
    @BindArray(R.array.commodities_list)
    String[] commoditiesCategoriesList;
    @BindColor(R.color.colorPrimaryDark)
    int colorPrimaryDark;
    @BindColor(R.color.colorTextDark)
    int colorTextDark;
    @BindColor(R.color.colorAccent)
    int colorAccent;
    @BindString(R.string.base_websocket_url)
    String baseWebSocketUrl;
    @BindFont(R.font.helvetica_bold)
    Typeface helveticaBoldTypeface;

    private Unbinder unbinder;
    private OkHttpClient okHttpClient;
    private ArrayList<String> parametersArrayList = new ArrayList<>();
    private CommoditiesParameterViewPagerAdapter commoditiesParameterViewPagerAdapter;
    private CommoditiesViewPagerAdapter commoditiesViewPagerAdapter;
    private MaterialDialog materialDialog;
    private Socket socket;
    private FetchCommoditiesNames fetchCommoditiesNames = new FetchCommoditiesNames();
    private FetchParameters fetchParameters = new FetchParameters();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(MainActivity.this);
        setSupportActionBar(mainToolbar);
        mainToolbar.setTitleTextColor(colorTextDark);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initializeComponents() {
        //if (!isMyServiceRunning()) startService(new Intent(getApplicationContext(), AlertService.class));
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .build();
        //monitorCommodities();
        fetchParameters.execute();
    }

    private void monitorCommodities() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String requestUrl = getApplicationContext().getString(R.string.mcx_live_price);
                    Request request = new Request.Builder().url(requestUrl).build();
                    Response response = okHttpClient.newCall(request).execute();
                    JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                    parseJsonTree(rootJson);
                } catch (IOException | JSONException e) {
                    Log.v("ALERT_SERV", e.getMessage());
                }
            }
        }, 0, 10000);
    }

    private void parseJsonTree(JSONObject rootJson) throws JSONException {
        JSONArray dataJsonArray = rootJson.getJSONArray("data");
        String previousSymbol = "";
        double previousPercentChange = 0;

        for (int i=0; i<dataJsonArray.length(); i++) {
            JSONObject jsonObject = dataJsonArray.getJSONObject(i);
            String symbol = jsonObject.getString("Symbol");
            if (symbol.equals("GOLD") || symbol.equals("SILVER") || symbol.equals("CRUDE OIL") || symbol.equals("COPPER") || symbol.equals("LEAD") || symbol.equals("NICKLE") || symbol.equals("ZINC") || symbol.equals("ALUMINIUM") || symbol.equals("NATURAL GAS") || symbol.equals("COTTON")) {
                String rawPercent = jsonObject.getString("% Change");
                rawPercent = rawPercent.replace("%", "");
                double percentChange = Double.parseDouble(rawPercent);

                if (percentChange>0.8 && !previousSymbol.equalsIgnoreCase(symbol)) {
                    previousSymbol = symbol;
                    previousPercentChange = Math.abs(percentChange);
                    Alerter.create(MainActivity.this)
                            .setTitle(symbol+" price increased by "+percentChange)
                            .setText("Commodities prices fluctuating")
                            .setIcon(R.drawable.report)
                            .setBackgroundColor(R.color.colorGreen)
                            .setDuration(1000)
                            .setOnClickListener(v -> {
                                Intent queryHashtagIntent = new Intent(MainActivity.this, QueryHashtagActivity.class);
                                queryHashtagIntent.putExtra("HASHTAG_DISPLAY", symbol);
                                queryHashtagIntent.putExtra("HASHTAG_TITLE", symbol+" commodity");
                                startActivity(queryHashtagIntent);
                            })
                            .show();
                    break;
                } else if (percentChange<-2.05 && !previousSymbol.equalsIgnoreCase(symbol)) {
                    previousSymbol = symbol;
                    previousPercentChange = Math.abs(percentChange);
                    Alerter.create(MainActivity.this)
                            .setTitle(symbol+" price decreased by "+Math.abs(percentChange))
                            .setText("Commodities prices fluctuating")
                            .setIcon(R.drawable.report)
                            .setBackgroundColor(R.color.colorDanger)
                            .setDuration(1000)
                            .setOnClickListener(v -> {
                                Intent queryHashtagIntent = new Intent(MainActivity.this, QueryHashtagActivity.class);
                                queryHashtagIntent.putExtra("HASHTAG_DISPLAY", symbol);
                                queryHashtagIntent.putExtra("HASHTAG_TITLE", symbol+" commodity");
                                startActivity(queryHashtagIntent);
                            })
                            .show();
                    break;
                }
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class FetchCommoditiesNames extends AsyncTask<String, Void, ArrayList<String>> {

        private String commodityGroupSelected;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mainProgressBar!=null) mainProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                commodityGroupSelected = strings[0];
                String requestUrl = baseServerUrl+"/commodities/names?query="+commodityGroupSelected;
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray commoditiesNamesJsonArray = rootJson.getJSONArray("commodities_names");
                ArrayList<String> commoditiesNamesArrayList = new ArrayList<>();
                for (int i=0; i<commoditiesNamesJsonArray.length(); i++) commoditiesNamesArrayList.add(commoditiesNamesJsonArray.getString(i));
                return commoditiesNamesArrayList;
            } catch (IOException | JSONException e) {
                Log.v("FETCH_COMMODITIES_NAMES", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            if (strings == null || strings.isEmpty())
                Snackbar.make(mainCommodityParametersViewPager, "There has been an error fetching data from the server", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", v -> fetchCommoditiesNames.execute(commodityGroupSelected))
                        .setActionTextColor(Color.YELLOW)
                        .show();
            else {
                commoditiesViewPagerAdapter = new CommoditiesViewPagerAdapter(getSupportFragmentManager(), strings);
                if(mainProgressBar!=null) mainProgressBar.setVisibility(View.GONE);
                mainCommodityParametersViewPager.setAdapter(commoditiesViewPagerAdapter);
                mainCommodityParametersTabLayout.setupWithViewPager(mainCommodityParametersViewPager);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchParameters extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mainProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            try {
                parametersArrayList.clear();
                String requestUrl = baseServerUrl + "/parameters";
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray jsonArray = jsonObject.getJSONArray("parameters");
                for (int i = 0; i < jsonArray.length(); i++)
                    parametersArrayList.add(jsonArray.getString(i));
                return parametersArrayList;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            if (strings == null || strings.isEmpty())
                Snackbar.make(mainCommodityParametersViewPager, "There has been an error fetching data from the server", Snackbar.LENGTH_LONG)
                .setAction("RETRY", v -> fetchParameters.execute())
                .setActionTextColor(Color.YELLOW)
                .show();
            else {
                commoditiesParameterViewPagerAdapter = new CommoditiesParameterViewPagerAdapter(getSupportFragmentManager(), strings);
                if(mainProgressBar!=null) mainProgressBar.setVisibility(View.GONE);
                mainCommodityParametersViewPager.setAdapter(commoditiesParameterViewPagerAdapter);
                mainCommodityParametersTabLayout.setupWithViewPager(mainCommodityParametersViewPager);
            }
        }
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menuItemSearch));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent queryHastagIntent = new Intent(MainActivity.this, QueryHashtagActivity.class);
                queryHastagIntent.putExtra("HASHTAG_DISPLAY", query);
                queryHastagIntent.putExtra("HASHTAG_TITLE", query);
                startActivity(queryHastagIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*
        if (id==R.id.menuItemChart) {
            materialDialog = new MaterialDialog.Builder(MainActivity.this)
                    .title(R.string.app_name)
                    .content("Choose a category of commodities")
                    .items(R.array.commodities_list)
                    .itemsCallback((dialog, itemView, position, text) -> {
                        text = text.toString().toLowerCase();
                        if (((String) text).contains("metals")) fetchCommoditiesNames.execute("Metals");
                        else if (((String) text).contains("grains")) fetchCommoditiesNames.execute("Grains");
                        else if (((String) text).contains("energy")) fetchCommoditiesNames.execute("Energy");
                        else fetchCommoditiesNames.execute("Meat");
                    })
                    .titleColor(Color.BLACK)
                    .contentColor(colorAccent)
                    .itemsColor(colorTextDark)
                    .show();

        }
        */
        return super.onOptionsItemSelected(item);
    }

    /*
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.mainNavigationItemImpact) fetchParameters.execute();
        else if (id==R.id.mainNavigationItemCompanies) startActivity(new Intent(MainActivity.this, CompanySearchActivity.class));
        else if (id==R.id.mainNavigationItemFuturesForecast) startActivity(new Intent(MainActivity.this, FuturesForecastActivity.class));
        else if (id==R.id.mainNavigationItemCommodityTracking) fetchCommoditiesNames.execute("Metals");
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    */

    @Override
    protected void onStart() {
        super.onStart();
        if (mainCommodityParametersTabLayout==null || mainCommodityParametersTabLayout.getTabCount()==0) new FetchParameters().execute();
        //if (!isMyServiceRunning()) startService(new Intent(getApplicationContext(), AlertService.class));
    }

    @Override
    protected void onStop() {
        if (fetchCommoditiesNames !=null && fetchCommoditiesNames.getStatus()== AsyncTask.Status.RUNNING)
            fetchCommoditiesNames.cancel(true);
        if (fetchParameters !=null && fetchParameters.getStatus()== AsyncTask.Status.RUNNING)
            fetchParameters.cancel(true);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        if (fetchCommoditiesNames !=null && fetchCommoditiesNames.getStatus()== AsyncTask.Status.RUNNING)
            fetchCommoditiesNames.cancel(true);
        if (fetchParameters !=null && fetchParameters.getStatus()== AsyncTask.Status.RUNNING)
            fetchParameters.cancel(true);
        super.onDestroy();
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (AlertService.class.getName().equals(service.service.getClassName())) return true;
        return false;
    }
}
