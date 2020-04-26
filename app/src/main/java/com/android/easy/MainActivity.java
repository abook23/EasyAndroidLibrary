package com.android.easy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.easy.ui.DownloadActivity;
import com.android.easy.ui.ListActivity;
import com.android.easy.ui.MarkdownActivity;
import com.android.easy.ui.http.HttpActivity;

/**
 * @author My.Y
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        Object[][] data = new Object[][]{
                {"http/retrofit2", "http.md", HttpActivity.class},
                {"上传/下载", "DownloadActivity.md", DownloadActivity.class},
                {"list", "listActivity.md", ListActivity.class},
                {"TEST", "test.md", HttpActivity.class},
        };

        recyclerView.setAdapter(new MyAdapter(data));
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHoder> {
        Object[][] data;

        public MyAdapter(Object[][] data) {
            this.data = data;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHoder(View.inflate(parent.getContext(), R.layout.item_main, null));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
            holder.mTextView.setText((String) data[position][0]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MarkdownActivity.class);
                    intent.putExtra("filePath", (String) data[position][1]);
                    startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return data.length;
        }


        class MyViewHoder extends RecyclerView.ViewHolder {

            public TextView mTextView;

            public MyViewHoder(@NonNull View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(R.id.text);
            }
        }
    }
}
