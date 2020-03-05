package abhishekwl.github.io.radar.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import abhishekwl.github.io.radar.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WebViewActivity extends AppCompatActivity {

    @BindView(R.id.webViewActivityWebView)
    WebView webView;

    private String url;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#FAFAFA"));
        getWindow().setNavigationBarColor(Color.parseColor("#FAFAFA"));
        setContentView(R.layout.activity_web_view);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(WebViewActivity.this);
    }

    private void initializeComponents() {
        url = getIntent().getStringExtra("URL");
        webView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
