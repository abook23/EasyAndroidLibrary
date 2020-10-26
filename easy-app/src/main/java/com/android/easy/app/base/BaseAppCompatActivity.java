package com.android.easy.app.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.android.easy.app.R;
import com.android.easy.base.net.NetworkManager;
import com.android.easy.base.util.PermissionUtil;


/**
 * 基类
 */
public class BaseAppCompatActivity extends HttpAppCompatActivity {

    private OnParameterChangeListener mOnParameterChangeListener;
    protected int REQUEST_CONTACTS = 0;
    private BaseAppBar mBaseAppBar;

    private View mNetworkView;
    private LinearLayout mContentViewLinearLayout, mContentBottomLayout;
    private boolean initAppBar = true;
    private View rootView;
    protected Context mContext;

    public void setContentView(int layoutResId, boolean initAppBar) {
        this.initAppBar = initAppBar;
        setContentView(layoutResId);
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(R.layout.easy_app_layout_content_view);
        mContext = this;
        contentLoadingView = findViewById(R.id.content_loading);
        mContentViewLinearLayout = findViewById(R.id.content_view);
        mContentBottomLayout = findViewById(R.id.content_bottom_layout);
        contentLoadingView.setVisibility(View.GONE);
//      LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rootView = LayoutInflater.from(this).inflate(layoutResId, mContentViewLinearLayout, false);
        mContentViewLinearLayout.addView(rootView, 0);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (initAppBar) {
            addAppBar();
        } else {
            setStatusBarTranslucentStatus();
            rootView.setPadding(0, getStatusBarHeight(), 0, 0);
        }
//      setTranslucentStatus(this);
//      setRootViewFitsSystemWindows(true);
        mContentViewLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
    }

    public void clearTranslucentStatusBarHeight() {
        rootView.setPadding(0, 0, 0, 0);
    }

    public void setAppBarTitle(String title) {
        getAppBar().setTitle(title, Color.WHITE);
    }

    public void showLoading(boolean b, String msg) {
        contentLoadingView.setVisibility(b ? View.VISIBLE : View.GONE);
        if (!b) {
            return;
        }
        contentLoadingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        TextView textView = findViewById(R.id.content_progress_text);
        textView.setText(msg);
    }

    /**
     * 获取appBar
     */
    public BaseAppBar getAppBar() {
        return mBaseAppBar;
    }

    /**
     * 获取appBar
     */
    public DefaultAppBar getDefaultAppBar() {
        if (mBaseAppBar instanceof DefaultAppBar) {
            return (DefaultAppBar) getAppBar();
        }
        return null;
    }

    public void setStatusBarTranslucentStatus() {
        setTranslucentStatus();
        if (mBaseAppBar != null) {
            mBaseAppBar.getRootView().setPadding(0, getStatusBarHeight(), 0, 0);
        }
    }

    private void addAppBar() {
        LinearLayout linearLayout = findViewById(R.id.content_app_bar);
        if (getAppBarLayout() != -1) {
            linearLayout.addView(LayoutInflater.from(this).inflate(getAppBarLayout(), linearLayout, false));
        } else {
            mBaseAppBar = onCreateAppBar(this, linearLayout);
            linearLayout.addView(mBaseAppBar.getRootView());
        }
    }

    /**
     * 全局 appbar
     *
     * @param viewGroup
     * @return
     */
    public BaseAppBar onCreateAppBar(Activity activity, ViewGroup viewGroup) {
        return new DefaultAppBar(activity, viewGroup);
    }

    /**
     * 当前页面自定义 appbar
     * 适用于 局部 appbar
     *
     * @return
     */
    public int getAppBarLayout() {
        return -1;
    }

    public void addNetworkListener() {
        NetworkManager.requestNetwork(this, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                showNetworkStatus(false);
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                showNetworkStatus(true);
            }
        });
    }

    public int getNetworkLayout() {
        return R.layout.easy_app_layout_network_status;
    }

    private void showNetworkStatus(final boolean b) {
        if (b && mNetworkView == null) {
            mNetworkView = LayoutInflater.from(this).inflate(getNetworkLayout(), mContentViewLinearLayout, false);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (b) {
                    mContentViewLinearLayout.addView(mNetworkView, 0);
                } else {
                    mContentViewLinearLayout.removeView(mNetworkView);
                }
            }
        });
    }




    public View addContentBottomView(@LayoutRes int layoutRes) {
        View view = LayoutInflater.from(this).inflate(layoutRes, mContentBottomLayout, false);
        addContentBottomView(view, 0);
        return view;
    }

    public void addContentBottomView(View view, int index) {
        mContentBottomLayout.setVisibility(View.VISIBLE);
        mContentBottomLayout.addView(view, index);
    }


    public View addContentView(@LayoutRes int layoutRes) {
        return addContentView(layoutRes, 0);
    }

    public View addContentView(@LayoutRes int layoutRes, int index) {
        View view = LayoutInflater.from(this).inflate(layoutRes, mContentBottomLayout, false);
        mContentViewLinearLayout.addView(view, index);
        return view;
    }

    /**
     * 权限申请 检测
     *
     * @param permissions
     * @param onParameterChangeListener
     */
    public void requestPermission(@NonNull String[] permissions, @NonNull OnParameterChangeListener onParameterChangeListener) {
        mOnParameterChangeListener = onParameterChangeListener;
        boolean statue = PermissionUtil.requestPermission(this, permissions, REQUEST_CONTACTS);
        onParameterChangeListener.onParameterChange(statue);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.verifyPermissions(this, permissions, grantResults)) {
            mOnParameterChangeListener.onParameterChange(true);
        } else {
            mOnParameterChangeListener.onParameterChange(false);
        }
    }

    public void setTranslucentStatus() {
        setTranslucentStatus(Color.TRANSPARENT);
    }


    /**
     * 设置状态栏透明
     */
    @TargetApi(19)
    public void setTranslucentStatus(@ColorInt int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = getWindow();
            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColor);
            //导航栏颜色也可以正常设置
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
            window.setAttributes(attributes);
        }

        //黑色字体
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 代码实现android:fitsSystemWindows
     *
     * @param fitSystemWindows 为true时会预留出状态栏高度
     */
    private void setRootViewFitsSystemWindows(boolean fitSystemWindows) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup winContent = findViewById(android.R.id.content);
            if (winContent.getChildCount() > 0) {
                ViewGroup rootView = (ViewGroup) winContent.getChildAt(0);
                if (rootView != null) {
                    rootView.setFitsSystemWindows(fitSystemWindows);
                }
            }
        }
    }

    public int getStatusBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public interface OnParameterChangeListener {
        void onParameterChange(boolean statue);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOnParameterChangeListener = null;
    }

    private Toast mToast;

    @SuppressLint("ShowToast")
    protected void showTost(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
            mToast.setText(msg);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void toFullscreen() {
        getAppBar().getRootView().setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
    }

    public void clearFullscreen() {
        getAppBar().getRootView().setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 取消全屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
