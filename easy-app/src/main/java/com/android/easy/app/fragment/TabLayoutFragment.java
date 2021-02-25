package com.android.easy.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.easy.app.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * author abook23@163.com
 * 2019/12/04
 */
public class TabLayoutFragment extends Fragment {

    public static final int MODE_SCROLLABLE = 0;//超出滑动
    public static final int MODE_FIXED = 1;//平均分布
    private int tableMode = TabLayout.MODE_SCROLLABLE;
    private View rootView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public int paddingTop = 0;

    private List<TabLayoutItem> mTabLayoutItemList;
    private TabLayout.BaseOnTabSelectedListener baseOnTabSelectedListener;

    private int resourceLayout, tabLayoutId, viewPagerId;
    private int tabLayoutItemResource;
    private Call mCall;

    public static TabLayoutFragment newInstance(@LayoutRes int resource, @IdRes int tabLayoutId, @IdRes int viewPagerId) {
        TabLayoutFragment tabLayoutFragment = new TabLayoutFragment();
        tabLayoutFragment.resourceLayout = resource;
        tabLayoutFragment.tabLayoutId = tabLayoutId;
        tabLayoutFragment.viewPagerId = viewPagerId;
        return tabLayoutFragment;
    }

    public static TabLayoutFragment getDefaultTabLayoutFragment() {
        TabLayoutFragment tabLayoutFragment = newInstance(R.layout.easy_app_default_fragment_tab_layout, R.id.tabLayout, R.id.viewPager);
        return tabLayoutFragment;
    }

    public void addFragments(Context context, @LayoutRes int layout, List<TabLayoutItem> itemList, Call call) {
        mCall = call;
        tabLayoutItemResource = layout;
        mTabLayoutItemList = itemList;
    }

    public void setTabMode(int mode) {
        tableMode = mode;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null && resourceLayout > 0) {
            rootView = LayoutInflater.from(getContext()).inflate(resourceLayout, container, false);
            if (paddingTop > 0)
                rootView.setPadding(0, paddingTop, 0, 0);
            tabLayout = rootView.findViewById(tabLayoutId);
            viewPager = rootView.findViewById(viewPagerId);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTabLayout();
    }

    public void addOnTabSelectedListener(TabLayout.BaseOnTabSelectedListener listener) {
        this.baseOnTabSelectedListener = listener;
    }

    private void initTabLayout() {

        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < mTabLayoutItemList.size(); i++) {
            TabLayoutItem item = mTabLayoutItemList.get(i);
            fragments.add(item.fragment);
        }

        addTabSelectedListener();
        viewPager.setAdapter(new TableLayoutViewPage(getChildFragmentManager(), fragments, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        tabLayout.setTabMode(tableMode);
//      tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View view = LayoutInflater.from(getContext()).inflate(tabLayoutItemResource, tab.parent, false);
            TabLayoutItem item = mTabLayoutItemList.get(i);
            mCall.convert(view, item, i);
            tabLayout.getTabAt(i).setCustomView(view);
        }
    }

    private void addTabSelectedListener() {
        if (baseOnTabSelectedListener != null) {
            tabLayout.addOnTabSelectedListener(baseOnTabSelectedListener);
        }
    }


    private static class TableLayoutViewPage extends FragmentPagerAdapter {

        private List<Fragment> mFragmentList;

        public TableLayoutViewPage(@NonNull FragmentManager fm, List<Fragment> fragmentList, int behavior) {
            super(fm, behavior);
            mFragmentList = fragmentList;
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
            return null;
        }
    }

    public static class TabLayoutItem {
        public String name;
        public int resId;
        public String url;
        public Fragment fragment;

        public TabLayoutItem(String name, int resId, Fragment fragment) {
            this.name = name;
            this.resId = resId;
            this.fragment = fragment;
        }
    }

    public interface Call {
        void convert(@NonNull View tabLayoutItemView, @NonNull TabLayoutItem item, int position);
    }
}

