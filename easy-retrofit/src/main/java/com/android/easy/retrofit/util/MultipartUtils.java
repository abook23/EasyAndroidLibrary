package com.android.easy.retrofit.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by abook23 on 2016/11/22.
 * Versions 1.0
 */

public class MultipartUtils {


    public static MultipartBody filesToMultipartBody(File file) {
        //MediaType.parse("text/x-markdown; charset=utf-8");
        //MediaType.parse("multipart/form-data")
        //MediaType.parse("application/octet-stream")

        MultipartBody.Builder builder = new MultipartBody.Builder();
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
        builder.addFormDataPart("file", file.getName(), requestBody);
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    public static MultipartBody filesToMultipartBody(List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (int i = 0; i < files.size(); i++) {
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            File file = files.get(i);
            if (file != null) {
//                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
                RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
                builder.addFormDataPart("file" + i, file.getName(), requestBody);
            }
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    public static MultipartBody filesToMultipartBody(Map<String, Object> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object o = entry.getValue();
            if (o instanceof File) {
                File file = (File) o;
                RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
                builder.addFormDataPart(key, file.getName(), requestBody);
            } else {
                builder.addFormDataPart(key, String.valueOf(o));
            }
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    public static MultipartBody filesToMultipartBody(File... files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (int i = 0; i < files.length; i++) {
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            File file = files[i];
            if (file != null) {
                RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
                builder.addFormDataPart("file" + i, file.getName(), requestBody);
            }
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    public static List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (int i = 0; i < files.size(); i++) {
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            File file = files.get(i);
            RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }
}
