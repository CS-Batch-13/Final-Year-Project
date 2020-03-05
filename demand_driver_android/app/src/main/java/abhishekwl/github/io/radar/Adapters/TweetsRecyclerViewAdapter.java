package abhishekwl.github.io.radar.Adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import abhishekwl.github.io.radar.Models.Tweet;
import abhishekwl.github.io.radar.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetsRecyclerViewAdapter extends RecyclerView.Adapter<TweetsRecyclerViewAdapter.TweetViewHolder> {

    private ArrayList<Tweet> tweetArrayList;

    public TweetsRecyclerViewAdapter(ArrayList<Tweet> tweetArrayList) {
        this.tweetArrayList = tweetArrayList;
        Collections.sort(this.tweetArrayList, (o1, o2) -> o2.getRetweetCount()-o1.getRetweetCount());
    }

    @NonNull
    @Override
    public TweetsRecyclerViewAdapter.TweetViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tweet_list_item, viewGroup, false);
        return new TweetViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TweetsRecyclerViewAdapter.TweetViewHolder tweetViewHolder, int i) {
        Tweet tweet = tweetArrayList.get(i);
        Glide.with(tweetViewHolder.itemView.getContext()).load(tweet.getUserImage()).into(tweetViewHolder.userImageView);
        Glide.with(tweetViewHolder.itemView.getContext()).load(R.drawable.retweet_icon).into(tweetViewHolder.reTweetIconImageView);
        Glide.with(tweetViewHolder.itemView.getContext()).load(R.drawable.twitter_logo).into(tweetViewHolder.logoImageView);
        tweetViewHolder.userNameTextView.setText(tweet.getUserName());
        tweetViewHolder.userFollowersCountTextView.setText(Integer.toString(tweet.getUserFollowersCount()).concat(" Followers"));
        tweetViewHolder.reTweetCountTextView.setText(Integer.toString(tweet.getRetweetCount()));
        tweetViewHolder.tweetContentTextView.setText(tweet.getContent());
    }

    @Override
    public int getItemCount() {
        return tweetArrayList.size();
    }

    class TweetViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tweetListItemUserNameTextView)
        TextView userNameTextView;
        @BindView(R.id.tweetListItemFollowersCount)
        TextView userFollowersCountTextView;
        @BindView(R.id.tweetListItemPostCreatedTextView)
        TextView postCreatedAtDate;
        @BindView(R.id.tweetListItemTweetContentTextView)
        TextView tweetContentTextView;
        @BindView(R.id.tweetListItemImageView)
        ImageView userImageView;
        @BindView(R.id.tweetListItemReTweetCount)
        TextView reTweetCountTextView;
        @BindView(R.id.tweetListItemReTweetImageView)
        ImageView reTweetIconImageView;
        @BindView(R.id.tweetListItemTwitterLogoImageView)
        ImageView logoImageView;

        TweetViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
