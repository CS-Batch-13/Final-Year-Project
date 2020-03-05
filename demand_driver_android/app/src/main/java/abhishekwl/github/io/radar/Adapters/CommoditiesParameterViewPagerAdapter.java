package abhishekwl.github.io.radar.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import abhishekwl.github.io.radar.Fragments.CommodityParameterFragment;

public class CommoditiesParameterViewPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<String> parametersArrayList;
    String title = null;

    public CommoditiesParameterViewPagerAdapter(FragmentManager fm, ArrayList<String> parametersArrayList) {
        super(fm);
        this.parametersArrayList = parametersArrayList;
    }

    public CommoditiesParameterViewPagerAdapter(FragmentManager supportFragmentManager, ArrayList<String> verticalsArrayList, String title) {
        super(supportFragmentManager);
        this.parametersArrayList = verticalsArrayList;
        this.title = title;
    }

    @Override
    public Fragment getItem(int i) {
        CommodityParameterFragment commodityParameterFragment = new CommodityParameterFragment();
        commodityParameterFragment.setParameterName(parametersArrayList.get(i));
        commodityParameterFragment.setParameterTitle(title);
        return commodityParameterFragment;
    }

    @Override
    public int getCount() {
        return parametersArrayList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return parametersArrayList.get(position);
    }
}
