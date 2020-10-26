package com.abook23.tv;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.easy.app.base.BaseAppCompatActivity;


public class SplashActivity extends BaseAppCompatActivity {
    private static String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private TextView countDownTextView;
    public int count = 3;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.isStart) {
            toActivity();
            return;
        }
        setContentView(R.layout.activity_splash, false);
        countDownTextView = findViewById(R.id.countDownTextView);
        countDownTextView.setOnClickListener(v -> {
            toActivity();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        App.isStart = true;
        requestPermission(PERMISSIONS, statue -> {
            countTime();
        });

    }

    private void countTime() {
        mCountDownTimer = new CountDownTimer(count * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDownTextView.post(() -> countDownTextView.setText("跳过 " + (millisUntilFinished / 1000 + 1)));
            }

            @Override
            public void onFinish() {
                toActivity();
            }
        };
        mCountDownTimer.start();
    }

    private void toActivity() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        startMainActivity();
        finish();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
