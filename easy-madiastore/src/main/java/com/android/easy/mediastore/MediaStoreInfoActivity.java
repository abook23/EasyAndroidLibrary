package com.android.easy.mediastore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.android.easy.mediastore.utils.LocalMedia;
import com.android.easy.mediastore.widget.CustPagerTransformer;
import com.android.easy.mediastore.widget.PhotoViewPager;
import com.android.easy.play.VideoFragment;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by abook23 on 2015/10/21.
 * <p>
 * 2016年10月28日 10:35:57  v2.0
 */
public class MediaStoreInfoActivity extends AppCompatActivity {

    public static String PATHS = "paths";
    public static String POSITION = "position";
    public static String TOKEN = "token";
    private PhotoViewPager viewPager;
    private List<String> paths = new ArrayList<>();
    private int position = 0;
    private String token;

    public static void start(Context context, String url) {
        ArrayList<String> paths = new ArrayList<>();
        paths.add(url);
        start(context, 0, paths);
    }

    public static void start(Context context, int position, ArrayList<String> urls) {
        Intent intent = new Intent(context, MediaStoreInfoActivity.class);
        intent.putExtra(MediaStoreInfoActivity.POSITION, position);
        intent.putStringArrayListExtra(MediaStoreInfoActivity.PATHS, urls);
        context.startActivity(intent);
    }

    public static void start(Context context, int position, ArrayList<String> urls, String token) {
        Intent intent = new Intent(context, MediaStoreInfoActivity.class);
        intent.putExtra(MediaStoreInfoActivity.TOKEN, token);
        intent.putExtra(MediaStoreInfoActivity.POSITION, position);
        intent.putStringArrayListExtra(MediaStoreInfoActivity.PATHS, urls);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esay_md_gallery_activity_image_info);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
        initView();
        initData();
    }

    private void initView() {
        viewPager = findViewById(R.id.gb_photo_viewpager);
    }

    private void initData() {
        paths = getIntent().getStringArrayListExtra(PATHS);
        position = getIntent().getIntExtra(POSITION, 0);
        token = getIntent().getStringExtra(TOKEN);
        viewPager.setPageTransformer(true, new CustPagerTransformer(this));
        //https://blog.csdn.net/qq_36486247/article/details/102531304
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                return getFragment(position);
            }

            @Override
            public int getCount() {
                return paths.size();
            }
        });
        viewPager.setCurrentItem(position);
    }

    private Fragment getFragment(int position) {
        List<LocalMedia> localMediaList = MediaStoreActivity.getCheckMediaList();
        if (localMediaList.size() > 0) {
            LocalMedia localMedia = localMediaList.get(position);
            if (localMedia.getMimeType().equals(MediaStoreConfig.MIME_TYPE_VIDEO)) {
                return getVideoPlayFragment("视频", localMedia.getPath());
            } else if (localMedia.getMimeType().equals(MediaStoreConfig.MIME_TYPE_AUDIO)) {
                return FragmentImage.newInstance(localMedia.getPath());
            } else {
                return FragmentImage.newInstance(localMedia.getPath());
            }
        } else {
            String url = paths.get(position);
            String name = url.substring(url.lastIndexOf("/") + 1);
            String type = name.substring(name.lastIndexOf(".") + 1);
            if (url.startsWith("http://") || url.startsWith("https://")) {
                url += "?token=" + token;
            }
            if (type.equals("mp3") || type.equals("mp4")) {
                return getVideoPlayFragment(name, url);
            } else {
                return FragmentImage.newInstance(url);
            }
        }
    }

    private VideoFragment getVideoPlayFragment(String videoName, String playUrl) {
        VideoFragment videoFragment = VideoFragment.newInstance(videoName,playUrl);
        return videoFragment;
    }

    public static class FragmentImage extends Fragment {
        private static final String ARG_PARAM1 = "param1";
        private String path;

        public FragmentImage() {
            // Required empty public constructor
        }

        public static FragmentImage newInstance(String path) {
            FragmentImage fragment = new FragmentImage();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, path);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                path = getArguments().getString(ARG_PARAM1);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.esay_md_fragment_photo, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            PhotoView photoView = view.findViewById(R.id.photoView);
            Glide.with(this).load(path).into(photoView);
        }
    }

}
