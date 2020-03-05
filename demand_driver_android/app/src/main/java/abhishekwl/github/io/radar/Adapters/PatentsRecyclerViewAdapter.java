package abhishekwl.github.io.radar.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import abhishekwl.github.io.radar.Models.Patent;
import abhishekwl.github.io.radar.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PatentsRecyclerViewAdapter extends RecyclerView.Adapter<PatentsRecyclerViewAdapter.PatentViewHolder> {

    private ArrayList<Patent> patentArrayList;

    public PatentsRecyclerViewAdapter(ArrayList<Patent> patentArrayList) {
        this.patentArrayList = patentArrayList;
    }

    @NonNull
    @Override
    public PatentsRecyclerViewAdapter.PatentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.patent_list_item, viewGroup, false);
        return new PatentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PatentsRecyclerViewAdapter.PatentViewHolder patentViewHolder, int i) {
        Patent patent = patentArrayList.get(i);
        patentViewHolder.titleTextView.setText(patent.getPatentTitle());
        String inventorsList = "";
        for (String inventor: patent.getInventorsNames()) inventorsList=inventor+",";
        inventorsList = inventorsList.substring(0, inventorsList.length()-1);
        patentViewHolder.inventorsNamesTextView.setText(inventorsList);
        patentViewHolder.countryCodeTextView.setText(patent.getPatentCountryCode());
        patentViewHolder.abstractTextView.setText(patent.getPatentAbstract());
        patentViewHolder.dateTextView.setText(patent.getPatentDate());
        patentViewHolder.dropDownImageView.setOnClickListener(v -> {
            if (patentViewHolder.abstractTextView.getVisibility()==View.VISIBLE) patentViewHolder.abstractTextView.setVisibility(View.GONE);
            else if (patentViewHolder.abstractTextView.getVisibility()==View.GONE) patentViewHolder.abstractTextView.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return patentArrayList.size();
    }

    class PatentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.patentListItemAbstractTextView)
        TextView abstractTextView;
        @BindView(R.id.patentListItemCountryTextView)
        TextView countryCodeTextView;
        @BindView(R.id.patentListItemDateTextView)
        TextView dateTextView;
        @BindView(R.id.patentListItemDropDownImageView)
        ImageView dropDownImageView;
        @BindView(R.id.patentListItemInventorsTextView)
        TextView inventorsNamesTextView;
        @BindView(R.id.patentListItemTitleTextView)
        TextView titleTextView;

        PatentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
