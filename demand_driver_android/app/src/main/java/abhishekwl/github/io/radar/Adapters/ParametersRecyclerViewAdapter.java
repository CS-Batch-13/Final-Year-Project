package abhishekwl.github.io.radar.Adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import abhishekwl.github.io.radar.Activities.AnalyzePostActivity;
import abhishekwl.github.io.radar.Activities.WebViewActivity;
import abhishekwl.github.io.radar.Models.Post;
import abhishekwl.github.io.radar.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ParametersRecyclerViewAdapter extends RecyclerView.Adapter<ParametersRecyclerViewAdapter.PostViewHolder> {

    private ArrayList<Post> postArrayList;
    private String categoryName;

    public ParametersRecyclerViewAdapter(ArrayList<Post> postArrayList, String categoryName) {
        this.categoryName = categoryName;
        Set<Post> postSet = new HashSet<>(postArrayList);
        postArrayList = new ArrayList<>();
        postArrayList.addAll(postSet);
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public ParametersRecyclerViewAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_list_item, viewGroup, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ParametersRecyclerViewAdapter.PostViewHolder postViewHolder, int i) {
        Post post = postArrayList.get(i);
        String postDescription = post.getDescription();
        if (postDescription!=null && !TextUtils.isEmpty(postDescription) && !postDescription.equalsIgnoreCase("null")) {
            if (post.getImage()==null || TextUtils.isEmpty(post.getImage()) || post.getImage().equalsIgnoreCase("null")) Glide.with(postViewHolder.itemView.getContext()).load(R.drawable.placeholder).into(postViewHolder.postImageView);
            else Glide.with(postViewHolder.itemView.getContext()).load(post.getImage()).into(postViewHolder.postImageView);
            Glide.with(postViewHolder.itemView.getContext()).load(R.drawable.internet).into(postViewHolder.visitWebsiteImageView);
            postViewHolder.postTitleTextView.setText(post.getTitle());
            postViewHolder.postDescriptionTextView.setText(post.getDescription());
            postViewHolder.postSourceNameTextView.setText(post.getSourceName());
            postViewHolder.postShareImageView.setOnClickListener(v -> {
                String shareContent = post.getTitle()+"\n"+post.getSourceName()+"\n\n\n"+post.getDescription()+"\n\n"+post.getUrl();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                postViewHolder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Share Post"));
            });
            /*
            postViewHolder.itemView.setOnClickListener(v -> {
                Intent analyzePostIntent = new Intent(postViewHolder.itemView.getContext(), AnalyzePostActivity.class);
                analyzePostIntent.putExtra("POST", post);
                if (categoryName!=null) analyzePostIntent.putExtra("CATEGORY", categoryName);
                postViewHolder.itemView.getContext().startActivity(analyzePostIntent);
            });
            */
            postViewHolder.visitWebsiteImageView.setOnClickListener(v -> {
                Intent webViewIntent = new Intent(postViewHolder.itemView.getContext(), WebViewActivity.class);
                webViewIntent.putExtra("URL", post.getUrl());
                postViewHolder.itemView.getContext().startActivity(webViewIntent);
            });
        } else postViewHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.postListItemImageView)
        ImageView postImageView;
        @BindView(R.id.analyzePostTitleTextView)
        TextView postTitleTextView;
        @BindView(R.id.analyzePostDescriptionTextView)
        TextView postDescriptionTextView;
        @BindView(R.id.analyzePostSourceNameTextView)
        TextView postSourceNameTextView;
        @BindView(R.id.analyzePostShareImageView)
        ImageView postShareImageView;
        @BindView(R.id.analyzePostVisitWebsiteImageView)
        ImageView visitWebsiteImageView;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
