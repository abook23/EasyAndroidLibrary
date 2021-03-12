package com.android.easy.app.base;

import android.annotation.SuppressLint;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.easy.retrofit.HttpCall;
import com.android.easy.retrofit.ApiService;

import java.util.Map;

public class HttpFragment extends Fragment {

    protected <T> void get(String url, Map<String, Object> params, HttpCall<T> call) {
        get(url, params, false, call);
    }

    protected <T> void get(String url, Map<String, Object> params, boolean showProgress, HttpCall<T> call) {
        ApiService.get(url, params, call);
    }

    protected <T> void post(String url, Map<String, Object> params, HttpCall<T> call) {
        post(url, params, false, call);
    }

    protected <T> void post(String url, Map<String, Object> params, boolean showProgress, HttpCall<T> call) {
        ApiService.post(url, params, call);
    }

    private Toast mToast;
    @SuppressLint("ShowToast")
    protected void showTost(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(getContext(), "", Toast.LENGTH_LONG);
            mToast.setText(msg);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
