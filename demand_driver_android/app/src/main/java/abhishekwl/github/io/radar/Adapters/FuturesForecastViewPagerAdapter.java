package abhishekwl.github.io.radar.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import abhishekwl.github.io.radar.Fragments.FuturesFragment;

public class FuturesForecastViewPagerAdapter extends FragmentStatePagerAdapter {

    private String[] commodityGroupsArray;

    public FuturesForecastViewPagerAdapter(FragmentManager fm, String[] commodityGroupsArray) {
        super(fm);
        this.commodityGroupsArray = commodityGroupsArray;
    }

    @Override
    public Fragment getItem(int i) {
        FuturesFragment futuresFragment = new FuturesFragment();
        futuresFragment.setCommodityGroup(commodityGroupsArray[i]);
        return futuresFragment;
    }

    @Override
    public int getCount() {
        return commodityGroupsArray.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return commodityGroupsArray[position];
    }
}
