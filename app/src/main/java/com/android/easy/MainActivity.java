package com.android.easy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.easy.mediastore.MediaStoreActivity;
import com.android.easy.ui.DownloadActivity;
import com.android.easy.ui.GridAdapter;
import com.android.easy.ui.ListActivity;
import com.android.easy.ui.ListViewActivity;
import com.android.easy.ui.LoginActivity;
import com.android.easy.ui.MarkdownActivity;
import com.android.easy.ui.dialog.DialogActivity;
import com.android.easy.ui.http.HttpActivity;

/**
 * @author My.Y
 */
public class MainActivity extends AppCompatActivity {

    GridAdapter mGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        Object[][] data = new Object[][]{
                {"http/retrofit2", HttpActivity.class, "http.md"},
                {"上传/下载", DownloadActivity.class, "DownloadActivity.md"},
                {"list", ListActivity.class, "listActivity.md"},
                {"相册", MediaStoreActivity.class},
                {"TEST", HttpActivity.class, "test.md"},
                {"LoginActivity", LoginActivity.class},
                {"ListViewActivity", ListViewActivity.class},
                {"EasyDialog", DialogActivity.class},
        };

        recyclerView.setAdapter(mGridAdapter = new GridAdapter(data));
        mGridAdapter.addItemClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (data[position].length < 3) {
                    Intent intent = new Intent(getApplicationContext(), (Class<?>) data[position][1]);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), MarkdownActivity.class);
                    intent.putExtra("filePath", (String) data[position][2]);
                    startActivity(intent);
                }
            }
        });
    }
}
