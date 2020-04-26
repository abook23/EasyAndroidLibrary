package com.android.easy.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.easy.R;
import com.zzhoujay.markdown.MarkDown;

import java.io.IOException;
import java.io.InputStream;

public class MarkdownActivity extends AppCompatActivity {

    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markdown);
        filePath = getIntent().getStringExtra("filePath");
        if (filePath == null) {
            filePath = "test.md";
        }
        TextView textView = findViewById(R.id.markdownTextView);
        textView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = getAssets().open(filePath);
                    Spanned spanned = MarkDown.fromMarkdown(inputStream, new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                            drawable.setBounds(0, 0, 400, 400);
                            return drawable;
                        }
                    }, textView);
                    textView.setText(spanned);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
