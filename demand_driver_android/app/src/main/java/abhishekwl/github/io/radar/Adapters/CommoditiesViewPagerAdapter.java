package abhishekwl.github.io.radar.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import abhishekwl.github.io.radar.Fragments.CommodityFragment;

public class CommoditiesViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> commoditiesNamesArrayList;

    public CommoditiesViewPagerAdapter(FragmentManager fm, ArrayList<String> commoditiesNamesArrayList) {
        super(fm);
        this.commoditiesNamesArrayList = commoditiesNamesArrayList;
    }

    @Override
    public Fragment getItem(int i) {
        CommodityFragment commodityFragment = new CommodityFragment();
        commodityFragment.setCommodityName(commoditiesNamesArrayList.get(i));
        return commodityFragment;
    }

    @Override
    public int getCount() {
        return commoditiesNamesArrayList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return commoditiesNamesArrayList.get(position);
    }
}
