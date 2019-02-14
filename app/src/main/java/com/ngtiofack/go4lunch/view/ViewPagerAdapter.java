package com.ngtiofack.go4lunch.view;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by <VOTRE-NOM> on <DATE-DU-JOUR>.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentListTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return FragmentPagerAdapter.POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        return mFragmentListTitles.get(position).hashCode();
    }

    @Override
    public int getCount() {
        return mFragmentListTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentListTitles.get(position);
    }

    public void addFragment(Fragment fragment, String Title) {
        mFragmentList.add(fragment);
        mFragmentListTitles.add(Title);
    }

    public void updateFragment(int pos, Fragment fragment, String title) {
        mFragmentList.remove(pos);
        mFragmentListTitles.remove(pos);
        mFragmentList.add(pos, fragment);
        mFragmentListTitles.add(pos, title);
        notifyDataSetChanged();
    }
}
