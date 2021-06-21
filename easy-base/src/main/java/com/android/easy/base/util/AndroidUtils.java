package com.android.easy.base.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.easy.base.listener.OnNetStatusListener;
import com.android.easy.base.net.NetworkManager;

import java.io.File;
import java.util.Locale;

/**
 * Created by abook23 on 2016/6/1.
 */
public class AndroidUtils {
    /**
     * 安装apk
     * 如果没有android.os.Process.killProcess(android.os.Process.myPid());完最后不会提示成、打开。
     * 如果没有i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);这一步的话，最后安装好了，点打开，是不会打开新版本应用的。
     * this.finish();
     *
     * @param file 要安装的apk的目录
     */
    @Deprecated
    public static void install(Context context, File file) {
        if (file != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @RequiresPermission(value = "android.permission.REQUEST_INSTALL_PACKAGES")
    public static boolean install(Context context, String filePath) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean installAllowed = context.getPackageManager().canRequestPackageInstalls();
            if (!installAllowed) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                install(context, filePath);
            }
        }

        try {
            if (TextUtils.isEmpty(filePath))
                return false;
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//增加读写权限
            }
            intent.setDataAndType(getPathUri(context, filePath), "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Uri getPathUri(Context context, String filePath) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String packageName = context.getPackageName();
            uri = FileProvider.getUriForFile(context, packageName + ".fileProvider", new File(filePath));
        } else {
            uri = Uri.fromFile(new File(filePath));
        }
        return uri;
    }

    /**
     * app 版本
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "未知版本";
        }

    }

    /**
     * app 版本
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 网络
     *
     * @deprecated See {@link NetworkManager}.
     */
    @Deprecated
    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isAvailable();
    }

    /**
     * 网络
     *
     * @deprecated See {@link NetworkManager}.
     */
    @Deprecated
    public static void getNetWorkType(Context context, OnNetStatusListener listener) {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        NetworkManager.NetworkType networkType;
        if (netInfo != null && netInfo.isAvailable()) {
            switch (netInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    networkType = NetworkManager.NetworkType.TYPE_CELLULAR;
//                    netName = netInfo.getSubtypeName();
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    networkType = NetworkManager.NetworkType.TYPE_WIFI;
//                    netName = netInfo.getTypeName();
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    networkType = NetworkManager.NetworkType.TYPE_ETHERNET;
                    break;
                default:
                    networkType = NetworkManager.NetworkType.TYPE_OTHER;
                    break;
            }
        } else {
            networkType = NetworkManager.NetworkType.TYPE_OTHER;
        }
        if (listener != null) {
            listener.onNetStatus(networkType);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void requestNetwork(Context context, ConnectivityManager.NetworkCallback networkCallback) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        NetworkRequest request = builder
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();
        mConnectivityManager.requestNetwork(request, networkCallback);
    }

    /**
     * GPS 是否打开
     * GPS 设置
     * Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
     * startActivityForResult(intent,0); //此为设置完成后返回到获取界面
     *
     * @param context
     * @return boolean    返回类型
     */
    public static boolean isGpsAvailable(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 浏览器下载文件
     *
     * @param context
     * @param urlStr
     */
    public static void downloadByBrowser(Context context, String urlStr) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlStr));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(Uri.parse(urlStr), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
     * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
     *
     * @param context
     * @param packageName
     * 应用程序的包名
     */
    private static final String SCHEME = "package";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
     */
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
     */
    private static final String APP_PKG_NAME_22 = "pkg";
    /**
     * InstalledAppDetails所在包名
     */
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    /**
     * InstalledAppDetails类名
     */
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    /**
     * app 设置
     *
     * @param context     c
     * @param packageName pn
     */
    public static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) {
            // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else {
            // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }


    public static void call(Activity activity, String number) {
        //用intent启动拨打电话
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            activity.startActivity(intent);
        }
    }


    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     * 更多请参考 https://www.jianshu.com/p/59440efa020c
     *
     * @return 手机IMEI
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                return tm.getImei();
            }
            return tm.getDeviceId();
        }
        return null;
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context    The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static String getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        Environment.isExternalStorageRemovable() ? Environment.getExternalStorageDirectory().getPath() :
                        context.getCacheDir().getPath();

        if (StringUtils.isEmpty(uniqueName))
            return cachePath;
        return cachePath + File.separator + uniqueName;
    }

    public static int dp2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }


    public static void settingGPS(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    public static void settingWIRELESS(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        context.startActivity(intent);
    }

    public static synchronized String selectCacheSize(Context context, boolean del) {
        long checkFileSize = 0;
        checkFileSize += countDirFileSize(context.getExternalCacheDir(), del);
        checkFileSize += countDirFileSize(context.getCacheDir(), del);
        return Formatter.formatFileSize(context, checkFileSize);
    }

    public static void deleteDirectory(File directory) {
        countDirFileSize(directory, true);
    }

    public static long countDirFileSize(File file, boolean del) {
        long fileSize = 0;
        if (file.isDirectory()) {
            File[] sub_files = file.listFiles();
            for (File sub_file : sub_files) {
                long length = countDirFileSize(sub_file, del);
                fileSize += length;
            }
        } else {
            fileSize += file.length();
            if (del && file.isFile() && file.exists()) {
                file.delete();
            }
        }
        return fileSize;
    }

    public static String getUriPath(Context context, Uri uri) {
        String path;
        if (!android.text.TextUtils.isEmpty(uri.getAuthority())) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (null == cursor) {
                Toast.makeText(context, "图片没找到", Toast.LENGTH_SHORT).show();
                return null;
            }
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        } else {
            path = uri.getPath();
        }
        return path;
    }

}
