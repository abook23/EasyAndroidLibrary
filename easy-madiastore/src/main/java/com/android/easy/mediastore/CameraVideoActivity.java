package com.android.easy.mediastore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CameraVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esay_md_activity_camera_video);
        CameraVideoFragment cameraVideoFragment = CameraVideoFragment.newInstance();
        cameraVideoFragment.setOnCameraVideoListener(new CameraVideoFragment.OnCameraVideoListener() {
            @Override
            public void onFragmentResult(String path, String type) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.cameraVideoFrameLayout, cameraVideoFragment)
//                .add(R.id.cameraVideoFrameLayout, CameraFragment.newInstance())
                .commit();
    }
}
