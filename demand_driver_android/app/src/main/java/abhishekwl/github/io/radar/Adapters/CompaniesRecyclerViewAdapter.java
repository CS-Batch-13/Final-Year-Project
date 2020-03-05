package abhishekwl.github.io.radar.Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.util.ArrayList;

import abhishekwl.github.io.radar.Activities.CompanyArticlesActivity;
import abhishekwl.github.io.radar.Models.Company;
import abhishekwl.github.io.radar.R;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CompaniesRecyclerViewAdapter extends RecyclerView.Adapter<CompaniesRecyclerViewAdapter.CompanyViewHolder> {

    private ArrayList<Company> companyArrayList;

    public CompaniesRecyclerViewAdapter(ArrayList<Company> companyArrayList) {
        this.companyArrayList = companyArrayList;
    }

    @NonNull
    @Override
    public CompaniesRecyclerViewAdapter.CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.company_list_item, viewGroup, false);
        return new CompanyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CompaniesRecyclerViewAdapter.CompanyViewHolder companyViewHolder, int i) {
        Company company = companyArrayList.get(i);
        companyViewHolder.bind(company, companyViewHolder.itemView);
    }

    @Override
    public int getItemCount() {
        return companyArrayList.size();
    }

    class CompanyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.companyListItemCompanyNameTextView)
        TextView nameTextView;
        @BindView(R.id.companyListItemFacebookImageView)
        ImageView facebookImageView;
        @BindView(R.id.companyListItemLinkedInImageView)
        ImageView linkedInImageView;
        @BindView(R.id.companyListItemLogoImageView)
        CircleImageView logoImageView;
        @BindView(R.id.companyListItemStateCountryTextView)
        TextView stateCountryTextView;
        @BindView(R.id.companyListItemTwitterImageView)
        ImageView twitterImageView;
        @BindView(R.id.companyListItemWebsiteTextView)
        TextView websiteTextView;
        @BindView(R.id.companyListItemYouTubeImageView)
        ImageView youtubeImageView;
        @BindString(R.string.contify_appid)
        String contifyAppId;
        @BindString(R.string.contify_appsecret)
        String contifyAppSecret;

        CompanyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void bind(Company company, View itemView) {
            GlideUrl glideUrl = new GlideUrl(company.getLogo(), new LazyHeaders.Builder().addHeader("APPID", contifyAppId).addHeader("APPSECRET", contifyAppSecret).build());
            Glide.with(itemView.getContext()).load(glideUrl).into(logoImageView);
            nameTextView.setText(company.getName());
            stateCountryTextView.setText(company.getHqState()+", ".concat(company.getHqCountry()));
            websiteTextView.setText(company.getUrl());

            Glide.with(itemView.getContext()).load(R.drawable.twitter_logo).into(twitterImageView);
            Glide.with(itemView.getContext()).load(R.drawable.facebook_logo).into(facebookImageView);
            Glide.with(itemView.getContext()).load(R.drawable.linkedin_logo).into(linkedInImageView);
            Glide.with(itemView.getContext()).load(R.drawable.youtube_logo).into(youtubeImageView);

            twitterImageView.setOnClickListener(v -> itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(company.getTwitter()))));
            facebookImageView.setOnClickListener(v -> itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(company.getFacebook()))));
            youtubeImageView.setOnClickListener(v -> itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(company.getYoutube()))));
            linkedInImageView.setOnClickListener(v -> itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(company.getLinkedIn()))));

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), CompanyArticlesActivity.class);
                intent.putExtra("COMPANY", company);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
