package abhishekwl.github.io.radar.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import abhishekwl.github.io.radar.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AffectedGovernmentPolicyRecyclerViewAdapter extends RecyclerView.Adapter<AffectedGovernmentPolicyRecyclerViewAdapter.AffectedGovernmentPolicy> {

    private ArrayList<String> policiesArrayList;

    public AffectedGovernmentPolicyRecyclerViewAdapter(ArrayList<String> policiesArrayList) {
        this.policiesArrayList = policiesArrayList;
    }

    @NonNull
    @Override
    public AffectedGovernmentPolicyRecyclerViewAdapter.AffectedGovernmentPolicy onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.government_policy_in_article_list_item, viewGroup, false);
        return new AffectedGovernmentPolicy(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AffectedGovernmentPolicyRecyclerViewAdapter.AffectedGovernmentPolicy affectedGovernmentPolicy, int i) {
        String policy = policiesArrayList.get(i);
        affectedGovernmentPolicy.policyTextView.setText(policy);
    }

    @Override
    public int getItemCount() {
        return policiesArrayList.size();
    }

    class AffectedGovernmentPolicy extends RecyclerView.ViewHolder {

        @BindView(R.id.affectedGovernmentPolicyListItemTextView)
        TextView policyTextView;

        AffectedGovernmentPolicy(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
