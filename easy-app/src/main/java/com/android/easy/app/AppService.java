package com.android.easy.app;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.android.easy.retrofit.ApiService;


public class AppService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AppService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        init();
    }

    private void init() {
        ApiService.init(this,"");

    }
}
