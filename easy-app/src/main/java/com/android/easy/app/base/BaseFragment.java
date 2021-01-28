package com.android.easy.app.base;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static android.content.Context.INPUT_METHOD_SERVICE;


public abstract class BaseFragment extends HttpFragment {

    private static final int KEY_requestCode = 100;
    private OnPermissionsListener mOnPermissionsListener;
    protected View rootView;
    private boolean isViewInitiated;
    private boolean isDataInitiated;

    protected abstract int getLayoutId();

    protected abstract void initView(@NonNull View rootView);


    protected abstract void onOneLoadData();

    protected abstract void onVisibleLoadData();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
        }
        return rootView;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return rootView.findViewById(id);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isViewInitiated) {
            initView(view);
        }
        isViewInitiated = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    public void loadData() {
        if (isViewInitiated && !isDataInitiated) {
            isDataInitiated = true;
            onOneLoadData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden() && isResumed()) {
            onVisibleLoadData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisibleLoadData();
        }
    }

    //权限判断和申请
    public void requestPermissions(String[] permissions,OnPermissionsListener onPermissionsListener) {
        //逐个判断是否还有未通过的权限
        mOnPermissionsListener = onPermissionsListener;
        boolean checkPermission = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                checkPermission = false;
            }
        }
        //有权限没有通过，需要申请
        if (!checkPermission) {
            requestPermissions(permissions, KEY_requestCode);
            return;
        }
        onPermissionsListener.onPermissions(true);

    }

    /**
     * 5.请求权限后回调的方法
     *
     * @param requestCode  是我们自己定义的权限请求码
     * @param permissions  是我们请求的权限名称数组
     * @param grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限
     *                     名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0)
            if (KEY_requestCode == requestCode) {
                boolean checkPermission = false;
                for (int i = 0; i < grantResults.length; i++) {
                    checkPermission = true;
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        checkPermission = false;
                    }
                }
                if (mOnPermissionsListener != null)
                    mOnPermissionsListener.onPermissions(checkPermission);
            }
    }

    public void hideSoftKeyboard() {
        if (getActivity() == null)
            return;
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    public interface OnPermissionsListener {
        void onPermissions(boolean statue);
    }
}
