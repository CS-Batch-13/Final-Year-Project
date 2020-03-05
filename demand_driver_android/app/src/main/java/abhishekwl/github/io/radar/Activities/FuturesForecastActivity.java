package abhishekwl.github.io.radar.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

import abhishekwl.github.io.radar.Adapters.FuturesForecastViewPagerAdapter;
import abhishekwl.github.io.radar.R;
import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;

public class FuturesForecastActivity extends AppCompatActivity {

    @BindView(R.id.futuresForecastTabLayout)
    TabLayout futuresForecastTabLayout;
    @BindView(R.id.futuresForecastViewPager)
    ViewPager futuresForecastViewPager;
    @BindString(R.string.base_server_url)
    String baseServerUrl;
    @BindArray(R.array.futures_categories)
    String[] futuresCategoriesArray;

    private Unbinder unbinder;
    private OkHttpClient okHttpClient;
    private FuturesForecastViewPagerAdapter futuresForecastViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_futures_forecast);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(FuturesForecastActivity.this);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#f44336\"><b>Futures Forecast</b></font>",Html.FROM_HTML_MODE_COMPACT));
        } else Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#f44336\"><b>Futures Forecast</b></font>"));
        futuresForecastViewPagerAdapter = new FuturesForecastViewPagerAdapter(getSupportFragmentManager(), futuresCategoriesArray);
        futuresForecastViewPager.setAdapter(futuresForecastViewPagerAdapter);
        futuresForecastTabLayout.setupWithViewPager(futuresForecastViewPager);
    }

    private void initializeComponents() {
        okHttpClient = new OkHttpClient();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
