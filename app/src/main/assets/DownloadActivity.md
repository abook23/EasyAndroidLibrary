## 普通上传
```
download("").subscribe(new ObserverBaseWeb<File>() {
            @Override
            public void onNext(File file) {

            }
        });
```
## 普通下载
```
upload("", new HashMap<String, Object>(), new HttpCall<Object>() {
            @Override
            public void onSuccess(@NonNull Object o) {

            }
        });
```
## 高级上传
``` 
UploadFile uploadFile = RetrofitHttp.upload("url", new HashMap<>(), new Call() {
            @Override
            public void onStart() {

            }

            @Override
            public void onPause() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onSize(long size, long maxSize) {
                view.setMax(maxSize);
                view.setProgress(size);
            }

            @Override
            public void onFail(Throwable e) {

            }

            @Override
            public void onSuccess(ResponseBody responseBody) {

            }

            @Override
            public void onCancel() {

            }
        });
    }
```
## 高级下载
``` 
DownloadFile downloadFile = RetrofitHttp.download("", new Call() {
            @Override
            public void onStart() {

            }

            @Override
            public void onPause() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onSize(long size, long maxSize) {
                view.setMax(maxSize);
                view.setProgress(size);
            }

            @Override
            public void onFail(Throwable e) {

            }

            @Override
            public void onSuccess(File file) {

            }

            @Override
            public void onCancel() {

            }
        });
```
