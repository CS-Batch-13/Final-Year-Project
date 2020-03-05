package abhishekwl.github.io.radar.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import abhishekwl.github.io.radar.Fragments.VerticalsFragment;

public class VerticalsViewPagerAdapter  extends FragmentStatePagerAdapter {

    private ArrayList<String> categoriesArrayList;
    private String place;
    private String title;

    public VerticalsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public VerticalsViewPagerAdapter(FragmentManager fm, ArrayList<String> categoriesArrayList, String place, String title) {
        super(fm);
        this.categoriesArrayList = categoriesArrayList;
        this.place = place;
        this.title = title;
    }

    @Override
    public Fragment getItem(int i) {
        VerticalsFragment verticalsFragment = new VerticalsFragment();
        verticalsFragment.setCategory(categoriesArrayList.get(i));
        verticalsFragment.setPlace(place);
        verticalsFragment.setTitle(title);
        return verticalsFragment;
    }

    @Override
    public int getCount() {
        return categoriesArrayList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return categoriesArrayList.get(position);
    }
}
