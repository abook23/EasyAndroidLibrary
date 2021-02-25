package com.android.easy.dialog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class EasyDialog extends DialogFragment {

    private Builder mBuilder;
    private ProgressBar mProgressBar;
    private TextView mProgressBar_percentage;
    private TextView mProgressBar_description;
    private EasyDialog mEasyDialog;
    private OnViewCreatedListener mOnViewCreatedListener;
    public RecyclerView mRecyclerView;
    private TextView mContent;
    private EditText editText;


    private EasyDialog(Builder builder) {
        this.mBuilder = builder;
    }


    public EasyDialog setOnViewCreatedListener(OnViewCreatedListener onViewCreatedListener) {
        mOnViewCreatedListener = onViewCreatedListener;
        return this;
    }

    public static class Builder {
        private CharSequence title;
        private CharSequence content;
        private CharSequence positive, positiveBig, negative;
        private OnClickListener positiveClickListener, negativeClickListener, positiveBigButtonClickListener;
        private int gravity = Gravity.CENTER;

        //进度 等待
        private boolean isProgress = false;
        private int progress = 0;
        private int progressMax = 100;
        private boolean isLoadProgress;
        private String progressDescription;

        //列表
        private List<String> arrays;
        private RecyclerView.Adapter<?> mAdapter;
        private AdapterView.OnItemClickListener mOnItemClickListener;
        private RecyclerView.LayoutManager layoutManager;

        //editView
        private boolean showEditView;
        private String editText;
        private String editHintText;
        private boolean canceledOnTouchOutside = true;
        private boolean cancelable = true;
        private String url;
        private OnDownloadListener downloadListener;
        private boolean downloadCancel, downloadCoverage;


        public Builder() {
        }

        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder title(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder content(CharSequence content) {
            this.content = content;
            return this;
        }

        public Builder positive(CharSequence positive, OnClickListener listener) {
            this.positive = positive;
            this.positiveClickListener = listener;
//            canceledOnTouchOutside = false;
            return this;
        }

        public Builder positiveBig(CharSequence positive, OnClickListener listener) {
            this.positiveBig = positive;
            this.positiveBigButtonClickListener = listener;
//            canceledOnTouchOutside = false;
            return this;
        }

        public Builder negative(CharSequence negative) {
            this.negative = negative;
            canceledOnTouchOutside = false;
            return this;
        }

        public Builder negative(CharSequence negative, OnClickListener listener) {
            this.negative = negative;
            this.negativeClickListener = listener;
            canceledOnTouchOutside = false;
            return this;
        }

        public Builder progress(boolean loopStyles) {
            isProgress = true;
            this.isLoadProgress = loopStyles;
            canceledOnTouchOutside = false;
            return this;
        }

        public Builder progress(int progress, int max) {
            isProgress = true;
            this.progress = progress;
            this.progressMax = max;
            this.isLoadProgress = false;
            this.canceledOnTouchOutside = false;
            return this;
        }

        public Builder progressDescription(String progressDescription) {
            this.progressDescription = progressDescription;
            return this;
        }

        public Builder showEditView(String text, String hintText) {
            this.showEditView = true;
            this.editText = text;
            this.editHintText = hintText;
            return this;
        }

        public Builder items(@NonNull List<String> arrays, AdapterView.OnItemClickListener onItemClickListener) {
            this.arrays = arrays;
            this.mOnItemClickListener = onItemClickListener;
            return this;
        }

        public Builder adapter(RecyclerView.Adapter<?> adapter, RecyclerView.LayoutManager layoutManager) {
            this.mAdapter = adapter;
            this.layoutManager = layoutManager;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean cancel) {
            this.canceledOnTouchOutside = cancel;
            return this;
        }

        public Builder cancelable(boolean flag) {
            this.cancelable = flag;
            return this;
        }

        public Builder download(String url, OnDownloadListener downloadListener) {
            return download(url, true, true, downloadListener);
        }

        public Builder download(String url, boolean cancel, boolean coverage, OnDownloadListener downloadListener) {
            isProgress = true;
            this.url = url;
            this.downloadCancel = cancel;
            this.downloadCoverage = coverage;
            this.downloadListener = downloadListener;
            return this;
        }

        public EasyDialog build() {
            return new EasyDialog(this);
        }
    }

    public EasyDialog show(FragmentManager manager) {
        show(manager, "BaseDialog");
        mEasyDialog = this;
        return this;
    }

    public int getProgressMax() {
        return mProgressBar.getMax();
    }

    public void setProgress(int progress, int max) {
        setProgress(progress, max, false);
    }

    public void setProgress(final long progress, final long max, final boolean formatFileSize) {
        if (mProgressBar == null || progress == 0)
            return;
        mProgressBar.post(new Runnable() {
            private String formatMaxFileSize;

            @Override
            public void run() {
                String percentage = String.format(Locale.getDefault(), "%.2f%s", (float) progress / max * 100, "%");
                mProgressBar.setProgress((int) progress);
                mProgressBar.setMax((int) max);
                mProgressBar_percentage.setText(percentage);
                if (formatFileSize && max > 0) {//max 100 是默认
                    if (formatMaxFileSize == null) {
                        formatMaxFileSize = formatFileSize(max);
                    }
                    mProgressBar_description.setText(formatFileSize(progress) + "/" + formatMaxFileSize);
                } else {
                    mProgressBar_description.setText(progress + "/" + max);
                }
            }
        });
    }

    private String formatFileSize(long sizeBytes) {
        return Formatter.formatFileSize(getContext(), sizeBytes);
    }

    public void setProgressBarFormat(String value) {
        mProgressBar_description.setText(value);
    }

    public String getEditValue() {
        if (editText == null) {
            return null;
        }
        return editText.getText().toString();
    }

    public void setContent(String content) {
        mContent.setText(content);
        mContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);//无标题栏
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.easy_app_base_dialog, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.title);
        title.setText(mBuilder.title);

        mContent = view.findViewById(R.id.content);
        if (mBuilder.content != null) {
            mContent.setText(mBuilder.content);
        } else {
            mContent.setVisibility(View.GONE);
        }

        TextView negativeView = view.findViewById(R.id.negativeView);
        TextView positiveView = view.findViewById(R.id.positiveView);
        Button positiveBigView = view.findViewById(R.id.positiveBigView);

        settingClickListener(negativeView, positiveView, positiveBigView);

        addProgressBar(view);

        addRecyclerView(view);

        addEditTextView(view);

        addDownloadFile(positiveView, negativeView);

        if (mOnViewCreatedListener != null)
            mOnViewCreatedListener.onViewCreated(this, view);

    }

    public void settingClickListener(TextView negativeView, TextView positiveView, Button positiveBigView) {
        if (mBuilder.negative != null) {
            negativeView.setText(mBuilder.negative);
            negativeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBuilder.negativeClickListener != null) {
                        mBuilder.negativeClickListener.onClick(mEasyDialog, v);
                    } else {
                        mEasyDialog.dismiss();
                    }
                }
            });
            mBuilder.cancelable = false;
        } else {
            negativeView.setVisibility(View.GONE);
        }

        if (mBuilder.positive != null) {
            positiveView.setText(mBuilder.positive);
            positiveView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBuilder.positiveClickListener.onClick(mEasyDialog, v);
                }
            });
        } else {
            positiveView.setVisibility(View.GONE);
        }

        if (mBuilder.positiveBig != null) {
            positiveBigView.setText(mBuilder.positiveBig);
            positiveBigView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBuilder.positiveBigButtonClickListener.onClick(mEasyDialog, v);
                }
            });
        } else {
            positiveBigView.setVisibility(View.GONE);
        }
    }

    private void addProgressBar(View view) {
        if (mBuilder.isProgress) {
            if (mBuilder.isLoadProgress) {
                view.findViewById(R.id.loopProgressBar).setVisibility(View.VISIBLE);
                view.findViewById(R.id.progressViewHorizontal).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.loopProgressBar).setVisibility(View.GONE);
                mProgressBar = view.findViewById(R.id.progressBar);
                mProgressBar_percentage = view.findViewById(R.id.easy_dialog_progress_percentage);
                mProgressBar_description = view.findViewById(R.id.easy_dialog_progress_description);
                setProgress(mBuilder.progress, mBuilder.progressMax);
                mProgressBar_description.setText(mBuilder.progressDescription);

            }
        } else {
            view.findViewById(R.id.loopProgressBar).setVisibility(View.GONE);
            view.findViewById(R.id.progressViewHorizontal).setVisibility(View.GONE);
        }
    }

    private void addRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerView);
        if (mBuilder.arrays != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setAdapter(new DefaultAdapter(mBuilder.arrays, mBuilder.mOnItemClickListener));
        } else if (mBuilder.mAdapter != null) {
            mRecyclerView.setLayoutManager(mBuilder.layoutManager);
            mRecyclerView.setAdapter(mBuilder.mAdapter);
        } else {
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    private void addEditTextView(View view) {
        editText = view.findViewById(R.id.edit_view);
        editText.setVisibility(View.GONE);
        if (mBuilder.showEditView) {
            editText.setVisibility(View.VISIBLE);
            editText.setText(mBuilder.editText);
            editText.setHint(mBuilder.editHintText);
        }
    }

    private void addDownloadFile(TextView positiveView, final TextView negativeView) {
        if (mBuilder.url != null) {
            positiveView.setVisibility(View.VISIBLE);
            positiveView.setText("下载");
            final DownloadFileManager downloadFileManager = DownloadFileManager.getInstance();
            positiveView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    downloadFileManager.downloadFile(getContext(), mBuilder.url, mBuilder.downloadCoverage, new DownloadFileManager.Call() {
                        @Override
                        public void onStart(long downloadId) {
                            setContent("正下载,请稍等...");
                            setProgress(0, 0);
                        }

                        @Override
                        public void onProgress(long progress, long max) {
                            setProgress(progress, max, true);
                        }

                        @Override
                        public void onComplete(String filePath) {
                            negativeView.setText("关闭");
                            mBuilder.downloadListener.onComplete(mEasyDialog, filePath);
                        }

                        @Override
                        public void onError(String msg) {
                            negativeView.setText("关闭");
                            setContent("下载失败");
                        }
                    });
                }
            });
            if (mBuilder.downloadCancel) {
                negativeView.setVisibility(View.VISIBLE);
                negativeView.setText("取消");
                negativeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadFileManager.cancel();
                        dismiss();
                    }
                });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = mBuilder.gravity;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        //            window.setDimAmount(0f);//覆盖成透明度

        getDialog().setCanceledOnTouchOutside(mBuilder.canceledOnTouchOutside);
        getDialog().setCancelable(mBuilder.cancelable);
    }

    public class DefaultAdapter extends RecyclerView.Adapter<DefaultAdapter.ViewHolder> {
        private List<String> mList;
        private AdapterView.OnItemClickListener mOnItemClickListener;

        public DefaultAdapter(@NonNull List<String> list, AdapterView.OnItemClickListener onItemClickListener) {
            mList = list;
            mOnItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.mTextView.setText(mList.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(null, v, position, 0);
                    }
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(android.R.id.text1);
            }
        }
    }


    public interface OnClickListener {
        void onClick(@NonNull EasyDialog dialog, @NonNull View view);
    }

    public interface OnDownloadListener {
        void onComplete(EasyDialog dialog, String filePath);
    }

    public interface OnViewCreatedListener {
        void onViewCreated(EasyDialog dialog, View view);
    }

    @RequiresPermission(value = Manifest.permission.REQUEST_INSTALL_PACKAGES)
    public boolean install(Context con, String filePath) {
        try {
            if (TextUtils.isEmpty(filePath))
                return false;
            File file = new File(filePath);
            if (!file.exists() || !file.getName().endsWith(".apk")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//增加读写权限
            }
            intent.setDataAndType(getPathUri(con, filePath), "application/vnd.android.package-archive");
            con.startActivity(intent);
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

}
