package com.android.easy.app.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.easy.app.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author abook23@163.com
 * 2019/12/04
 */
public class TabLayoutFragment extends Fragment {

    public static final int MODE_SCROLLABLE = 0;//滑动
    public static final int MODE_FIXED = 1;//平均分布

    private Map<String, Fragment> mFragmentMap = new HashMap<>();
    private boolean isVisibleToUser;
    private View rootView;

    private int tableMode = TabLayout.MODE_SCROLLABLE;
    private boolean isInitData;
    private boolean isCreate;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public int paddingTop = 0;
    private List<View> mTabViewIconViews;

    public static TabLayoutFragment newInstance() {
        return newInstance(new HashMap<String, Fragment>(), 0);
    }

    public static TabLayoutFragment newInstance(Map<String, Fragment> fragmentMap) {
        return newInstance(fragmentMap, 0);
    }

    public static TabLayoutFragment newInstance(Map<String, Fragment> fragmentMap, int paddingTop) {
        TabLayoutFragment fragment = new TabLayoutFragment();
        fragment.setFragments(fragmentMap);
        fragment.paddingTop = paddingTop;
        return fragment;
    }


    public void addFragment(View tabViewIcon, Fragment fragment) {
        if (mTabViewIconViews == null) {
            mTabViewIconViews = new ArrayList<>();
        }
        mTabViewIconViews.add(tabViewIcon);
        mFragmentMap.put(mFragmentMap.size() + "table", fragment);
    }


    private void setFragments(Map<String, Fragment> fragmentMap) {
        mFragmentMap = fragmentMap;
    }

    public void setTabMode(int mode) {
        tableMode = mode;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCreate = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.easy_app_fragment_tab_layout, container, false);
            if (paddingTop > 0) {
                rootView.setPadding(0, paddingTop, 0, 0);
            }
            tabLayout = rootView.findViewById(R.id.tabLayout);
            viewPager = rootView.findViewById(R.id.viewPager);
            if (isVisibleToUser && isCreate && !isInitData) {
                initData();
            }
        }
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && isCreate && !isInitData) {
            initData();
        }
    }


    private void initData() {
        isInitData = true;
        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (Map.Entry<String, Fragment> entry : mFragmentMap.entrySet()) {
            fragments.add(entry.getValue());
            titles.add(entry.getKey());
        }
        if (mTabViewIconViews != null) {//自定义图标
            for (int i = 0; i < mTabViewIconViews.size(); i++) {
                tabLayout.getTabAt(i).setCustomView(mTabViewIconViews.get(i));
            }
        }
        viewPager.setAdapter(new TableLayoutViewPage(getChildFragmentManager(), titles, fragments, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        tabLayout.setTabMode(tableMode);
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView textView = new TextView(getContext());
                textView.setText(tab.getText());
                textView.setTextSize(26);
                textView.setTextColor(getResources().getColor(R.color.text_color));
                tab.setCustomView(textView);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

    }

    public class TableLayoutViewPage extends FragmentPagerAdapter {

        private List<Fragment> mFragmentList;
        private List<String> mTitles;

        public TableLayoutViewPage(@NonNull FragmentManager fm, List<String> titles, List<Fragment> fragmentList, int behavior) {
            super(fm, behavior);
            mFragmentList = fragmentList;
            mTitles = titles;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }
}
