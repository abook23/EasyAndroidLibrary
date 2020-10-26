package com.android.easy.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
 * @author abook23@163.com
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

    private List<View> mTabViewIconList;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private TabLayout.BaseOnTabSelectedListener baseOnTabSelectedListener;

    private int resourceLayout, tabLayoutId, viewPagerId;

    public static TabLayoutFragment newInstance(@LayoutRes int resource, @IdRes int tabLayoutId, @IdRes int viewPagerId) {
        TabLayoutFragment tabLayoutFragment = new TabLayoutFragment();
        tabLayoutFragment.resourceLayout = resource;
        tabLayoutFragment.tabLayoutId = tabLayoutId;
        tabLayoutFragment.viewPagerId = viewPagerId;
        return tabLayoutFragment;
    }

    public static TabLayoutFragment getDefaultTabLayoutFragment() {
        TabLayoutFragment tabLayoutFragment = newInstance(R.layout.easy_app_default_fragment_tab_layout,
                R.id.tabLayout, R.id.viewPager);
        return tabLayoutFragment;
    }

    public void addFragment(View tabViewIcon, Fragment fragment) {
        if (mTabViewIconList == null) {
            mTabViewIconList = new ArrayList<>();
        }
        mTabViewIconList.add(tabViewIcon);
        mFragmentList.add(fragment);
    }

    public void addFragments(Context context, @LayoutRes int layout, List<TabLayoutItem> datas, Call call) {
        for (int i = 0; i < datas.size(); i++) {
            TabLayoutItem item = datas.get(i);
            View view = LayoutInflater.from(context).inflate(layout, null, false);
            call.convert(view, item, i);
            addFragment(view, item.fragment);
        }
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
        addTabSelectedListener();
        viewPager.setAdapter(new TableLayoutViewPage(getChildFragmentManager(),mFragmentList, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        tabLayout.setTabMode(tableMode);
//      tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        tabLayout.setupWithViewPager(viewPager);
        if (mTabViewIconList != null) {//自定义图标
            for (int i = 0; i < mTabViewIconList.size(); i++) {
                tabLayout.getTabAt(i).setCustomView(mTabViewIconList.get(i));
            }
        }

    }

    private void addTabSelectedListener() {
        if (baseOnTabSelectedListener != null) {
            tabLayout.addOnTabSelectedListener(baseOnTabSelectedListener);
        }
    }


    private static class TableLayoutViewPage extends FragmentPagerAdapter {

        private List<Fragment> mFragmentList;

        public TableLayoutViewPage(@NonNull FragmentManager fm,List<Fragment> fragmentList, int behavior) {
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

//        @Nullable
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mTitles.get(position);
//        }
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

