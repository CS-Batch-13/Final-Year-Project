package abhishekwl.github.io.radar.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import abhishekwl.github.io.radar.Adapters.VerticalsViewPagerAdapter;
import abhishekwl.github.io.radar.Models.Post;
import abhishekwl.github.io.radar.R;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VerticalsActivity extends AppCompatActivity {

    @BindArray(R.array.verticals)
    String[] verticalsArray;
    @BindView(R.id.verticalsTabLayout)
    TabLayout verticalsTabLayout;
    @BindView(R.id.verticalsViewPager)
    ViewPager verticalsViewPager;

    private Unbinder unbinder;
    private Post selectedPost;
    private VerticalsViewPagerAdapter verticalsViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verticals);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(VerticalsActivity.this);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        selectedPost = getIntent().getParcelableExtra("POST");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#f44336\"><b>" + selectedPost.getTitle() + "</b></font>", Html.FROM_HTML_MODE_COMPACT));
            Objects.requireNonNull(getSupportActionBar()).setSubtitle(Html.fromHtml("<font color=\"#f44336\"><b><i>VERTICALS</i></b></font>", Html.FROM_HTML_MODE_COMPACT));
        } else
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#f44336\"><b>" + selectedPost.getTitle() + "</b></font>"));
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(Html.fromHtml("<font color=\"#f44336\"><b><i>VERTICALS</i></b></font>"));
    }

    private void initializeComponents() {
        ArrayList<String> verticalsArrayList = new ArrayList<>(Arrays.asList(verticalsArray));
        verticalsViewPagerAdapter = new VerticalsViewPagerAdapter(getSupportFragmentManager(), verticalsArrayList, selectedPost.getSourceId(), selectedPost.getTitle());
        verticalsViewPager.setAdapter(verticalsViewPagerAdapter);
        verticalsTabLayout.setupWithViewPager(verticalsViewPager);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
