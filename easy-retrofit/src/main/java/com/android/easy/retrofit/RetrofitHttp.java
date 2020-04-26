package com.android.easy.retrofit;

import com.android.easy.retrofit.listener.Call;
import com.android.easy.retrofit.progress.DownloadFile;
import com.android.easy.retrofit.progress.UploadFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author abook23@163.com
 * @date 2020/04/03
 */
public class RetrofitHttp extends ApiService {

    public static  <T> void get(String url, Call<T> call) {
        ApiService.get(url, new HashMap<String, Object>(), call);
    }

    public static UploadFile upload(String url, Map<String, Object> params, com.android.easy.retrofit.listener.loading.Call call) {
        return FileService.upload(url, params, call);
    }

    public static DownloadFile download(String url, com.android.easy.retrofit.listener.download.Call call) {
        return FileService.download(url, call);
    }
}
