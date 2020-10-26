package com.android.easy.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.easy.R;
import com.android.easy.base.util.AndroidUtils;
import com.android.easy.dialog.EasyDialog;
import com.android.easy.ui.GridAdapter;

import java.io.File;

public class DialogActivity extends AppCompatActivity {

    GridAdapter mGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        Object[][] data = new Object[][]{
                {"MsgDialog"},
                {"LoadingDialog"},
                {"ProgressDialog"},
                {"EditDialog"},
                {"RecyclerDialog"},
                {"DownloadDialog"},
        };
        recyclerView.setAdapter(mGridAdapter = new GridAdapter(data));
        mGridAdapter.addItemClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EasyDialog.Builder builder = new EasyDialog.Builder();
                switch (position) {
                    case 0:
                        builder.title("消息提示").content("小白篇");
                        break;
                    case 1:
                        builder.title("消息提示").content("加载中,请稍等...").progress(true);
                        break;
                    case 2:
//                        builder.title("消息提示").content("加载中,请稍等...").progress(false);
                        builder.title("消息提示").content("加载中,请稍等...").progress(false).progress(45, 100);
                        builder.positive("确定", new EasyDialog.OnClickListener() {
                            @Override
                            public void onClick(EasyDialog dialog, View view) {
                                dialog.dismiss();
                            }
                        }).negative("取消", new EasyDialog.OnClickListener() {
                            @Override
                            public void onClick(EasyDialog dialog, View view) {
                                dialog.dismiss();
                            }
                        });
                        break;
                    case 3:
                        builder.title("电话号码输出").showEditView(null, "请输入电话号码");
                        builder.positive("确定", new EasyDialog.OnClickListener() {
                            @Override
                            public void onClick(EasyDialog dialog, View view) {
                                dialog.dismiss();
                            }
                        }).negative("取消", new EasyDialog.OnClickListener() {
                            @Override
                            public void onClick(EasyDialog dialog, View view) {
                                dialog.dismiss();
                            }
                        });
                        break;
                    case 4:
                        builder.title("RecyclerDialog").adapter(mGridAdapter, new GridLayoutManager(DialogActivity.this, 3));
                        break;
                    case 5:
                        builder.title("下载提示").content("版本更新").progressDescription("50MB").canceledOnTouchOutside(false);
                        builder.download("http://sj1.kddf.com:8001/down/arcore.apk",false,false, new EasyDialog.OnDownloadListener() {
                            @Override
                            public void onComplete(EasyDialog dialog, String filePath) {
                                Toast.makeText(DialogActivity.this, filePath, Toast.LENGTH_SHORT).show();
//                                dialog.install(DialogActivity.this, filePath);
//                                dialog.dismiss();
                            }
                        });
                }
                builder.build().show(getSupportFragmentManager());
            }
        });
    }
}