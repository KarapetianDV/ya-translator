package ru.karapetiandav.ya_translator.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.karapetiandav.ya_translator.R;

public class PagerFragment extends Fragment {

    PagerAdapter pagerAdapter;
    ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pager, container, false);

        // Из-за Nested Fragment надо использовать именно этот менеджер
        pagerAdapter = new PagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);

        return view;
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new Fragment();

            if(position == 0) {
                fragment = new HistoryFragment();
            }
            if(position == 1) {
                fragment = new FavoriteFragment();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String tabTitle = "";

            if(position == 0) {
                tabTitle = "История";
            }
            if(position == 1) {
                tabTitle = "Избранное";
            }

            return tabTitle;
        }
    }
}
