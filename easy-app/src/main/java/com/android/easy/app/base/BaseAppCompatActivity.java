package com.android.easy.app.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.android.easy.app.R;
import com.android.easy.base.net.NetworkUtils;
import com.android.easy.base.util.PermissionUtil;


/**
 * 基类
 */
public class BaseAppCompatActivity extends HttpAppCompatActivity implements BaseAppBar.OnAppBarListener {

    private OnParameterChangeListener mOnParameterChangeListener;
    protected int REQUEST_CONTACTS = 0;
    private BaseAppBar mBaseAppBar;

    private View mNetworkView;
    private LinearLayout mContentViewLinearLayout;
    private boolean initAppBar = true;

    public void setContentView(int layoutResId, boolean initAppBar) {
        this.initAppBar = initAppBar;
        setContentView(layoutResId);
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(R.layout.layout_content_view);
        httpProgressView = findViewById(R.id.content_progress);
        mContentViewLinearLayout = findViewById(R.id.content_view);
//      LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mContentViewLinearLayout.addView(LayoutInflater.from(this).inflate(layoutResId, mContentViewLinearLayout, false), 0);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (initAppBar) {
            addAppBar();
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

    public void setAppBarTitle(String title) {
        getAppBar().setTitle(title, Color.WHITE);
    }

    public void showLoading(boolean b, String msg) {
        httpProgressView.setVisibility(b ? View.VISIBLE : View.GONE);
        if (!b) {
            return;
        }
        httpProgressView.setOnTouchListener((v, event) -> true);
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
        setTranslucentStatus(this);
        mBaseAppBar.getRootView().setPadding(0, getStatusBarHeight(), 0, 0);
    }

    private void addAppBar() {
        LinearLayout linearLayout = findViewById(R.id.content_app_bar);
        if (getAppBarLayout() != -1) {
            linearLayout.addView(LayoutInflater.from(this).inflate(getAppBarLayout(), linearLayout, false));
        } else {
            mBaseAppBar = onCreateAppBar(linearLayout);
            linearLayout.addView(mBaseAppBar.getRootView());
        }
    }

    /**
     * 全局 appbar
     *
     * @param viewGroup
     * @return
     */
    public BaseAppBar onCreateAppBar(ViewGroup viewGroup) {
        BaseAppBar baseAppBar = new DefaultAppBar(viewGroup);
        baseAppBar.setOnAppBarListener(this);
        return baseAppBar;
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
        NetworkUtils.requestNetwork(this, new ConnectivityManager.NetworkCallback() {
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
        return R.layout.layout_network_status;
    }

    private void showNetworkStatus(boolean b) {
        if (b && mNetworkView == null) {
            mNetworkView = LayoutInflater.from(this).inflate(getNetworkLayout(), mContentViewLinearLayout, false);
        }
        runOnUiThread(() -> {
            if (b) {
                mContentViewLinearLayout.addView(mNetworkView, 0);
            } else {
                mContentViewLinearLayout.removeView(mNetworkView);
            }
        });
    }

    public void addView(View view, int index) {
        mContentViewLinearLayout.addView(view, index);
    }

    public void removeView(View view) {
        mContentViewLinearLayout.removeView(view);
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

    public static void setTranslucentStatus(Activity activity) {
        setTranslucentStatus(activity, Color.TRANSPARENT);
    }


    /**
     * 设置状态栏透明
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity, @ColorInt int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColor);
            //导航栏颜色也可以正常设置
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
            window.setAttributes(attributes);
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

    private int getStatusBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }


    @Override
    public void onNavigationClick(View view) {
        finish();
    }

    @Override
    public void onAppBarClick(View view) {

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
}
