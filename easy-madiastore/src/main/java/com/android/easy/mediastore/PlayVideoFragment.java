package com.android.easy.mediastore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.android.easy.mediastore.R;
import com.android.easy.play.VideoFragment;

public class PlayVideoFragment extends Fragment {
    private String videoName, playUrl;

    public PlayVideoFragment() {
        // Required empty public constructor
    }

    public static PlayVideoFragment newInstance(String videoName, String playUrl) {
        PlayVideoFragment fragment = new PlayVideoFragment();
        fragment.playUrl = playUrl;
        fragment.videoName = videoName;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_video, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        VideoFragment videoFragment = getVideoPlayFragment(videoName, playUrl);
        getChildFragmentManager().beginTransaction().add(R.id.videoFrameLayout, videoFragment).commit();
    }

    private VideoFragment getVideoPlayFragment(String videoName, String playUrl) {
        return VideoFragment.newInstance(videoName,playUrl);
    }
}
