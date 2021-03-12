package com.android.easy.mediastore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.easy.mediastore.utils.LocalMedia;
import com.android.easy.mediastore.utils.LocalMediaFolder;
import com.android.easy.mediastore.utils.LocalMediaLoader;
import com.android.easy.mediastore.utils.MediaMode;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * author My.Y
 */
public class MediaStoreActivity extends AppCompatActivity implements LocalMediaLoader.LocalMediaLoadListener {

    public static String CHECK_MAX = "checkCount";//能选中多少张
    public static String DATA = "data";//返回值
    private final String CAMERA_PATH = "CAMERA_PATH";
    private static MediaStoreConfig mediaStoreConfig;

    private final int PERMISSION_CODE = 1;

    private MediaStoreAdapter mMediaStoreAdapter;
    private GalleryAdapter mGalleryAdapter;
    private RecyclerView mGalleryRecyclerView;
    public static List<LocalMedia> mCheckMediaList = new ArrayList<>();//选择的
    private List<LocalMediaFolder> mLocalMediaFolders;

    private Button mediaStoreSuccessButton;
    private TextView mediaStoreFolder;
    private Context context;
    private View appBarView;
    private MediaStoreFolderFragment mMediaStoreFolderFragment;
    private int CAMERA_VIDEO_CODE = 0x01;

    public static void startActivityForResult(Activity ac, int checkMax, int resultCode) {
        startActivityForResult(ac, checkMax, MediaMode.TYPE_IMAGE_VIDEO, resultCode);
    }

    public static void startActivityForResult(Activity ac, int checkMax, MediaMode mediaMode, int resultCode) {
        mediaStoreConfig = new MediaStoreConfig();
        mediaStoreConfig.mediaMode = mediaMode;
        startActivityForResult(ac, checkMax, mediaStoreConfig, resultCode);
    }

    public static void startActivityForResult(Activity ac, int checkMax, MediaStoreConfig cf, int resultCode) {
        mediaStoreConfig = cf;
        Intent intent = new Intent(ac, MediaStoreActivity.class);
        intent.putExtra(CHECK_MAX, checkMax);
        ac.startActivityForResult(intent, resultCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esay_md_activity_media_store);
        context = this;
        initView();
        addListener();
        mCheckMediaList = new ArrayList<>();
        if (mediaStoreConfig == null) {
            mediaStoreConfig = new MediaStoreConfig();
        }
        initDate();
    }

    public void initView() {
        mediaStoreFolder = findViewById(R.id.mediaStoreFolder);
        mediaStoreSuccessButton = findViewById(R.id.mediaStoreSuccess);
        appBarView = findViewById(R.id.appBar);

        RecyclerView mediaRecyclerView = findViewById(R.id.mediaRecyclerView);
        mediaRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mediaRecyclerView.setAdapter(mMediaStoreAdapter = new MediaStoreAdapter());

        mGalleryRecyclerView = findViewById(R.id.mediaRecyclerViewGallery);
        mGalleryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mGalleryRecyclerView.setAdapter(mGalleryAdapter = new GalleryAdapter());
        mGalleryRecyclerView.setVisibility(View.GONE);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelperCallback(new RecyclerItemTouchHelperCallback.OnItemTouchListener() {
            @Override
            public void onItemMove(int form, int to) {
                Collections.swap(mCheckMediaList, form, to);
            }

            @Override
            public void onItemMoved(int form, int to) {
                mGalleryAdapter.notifyItemMoved(form, to);
            }

            @Override
            public void onItemDelete(int position) {
                mCheckMediaList.remove(position);
                notifyDataGalleryRecyclerView();
                mMediaStoreAdapter.notifyDataSetChanged();
            }
        }));
        itemTouchHelper.attachToRecyclerView(mGalleryRecyclerView);
//        DefaultItemAnimator animator = new DefaultItemAnimator();
//        animator.setAddDuration(220);
//        animator.setRemoveDuration(200);
//        mGalleryRecyclerView.setItemAnimator(animator);
    }

    private void initDate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            loadAllMedia();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
    }

    public void addListener() {
        //选择完成
        mediaStoreSuccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResult();
            }
        });
        //切换图片库
        mediaStoreFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showFolderPopupWindow(appBarView);
                showFolderFragment();
            }
        });
    }

    //返回
    public void onBackClick(View view) {
        finish();
    }

    private void showFolderFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMediaStoreFolderFragment == null) {
            mMediaStoreFolderFragment = MediaStoreFolderFragment.newInstance(mLocalMediaFolders,
                    new MediaStoreFolderFragment.OnSelectFolderListener() {
                        @Override
                        public void onSelectFolder(LocalMediaFolder localMediaFolder) {
                            mediaStoreFolder.setText(localMediaFolder.getName());
                            mMediaStoreAdapter.setData(localMediaFolder.getImages());
                            mMediaStoreAdapter.notifyDataSetChanged();
                            getSupportFragmentManager().beginTransaction().remove(mMediaStoreFolderFragment).commit();
                        }
                    });
        }
        if (!mMediaStoreFolderFragment.isAdded()) {
            fragmentTransaction.add(R.id.folderFrameLayout, mMediaStoreFolderFragment, "folderMediaStoreFragment").commit();
        } else {
            fragmentTransaction.remove(mMediaStoreFolderFragment).commit();
        }
    }

    public static List<LocalMedia> getCheckMediaList() {
        return mCheckMediaList;
    }

    public static ArrayList<String> getCheckMediaPathList() {
        ArrayList<String> checkMedia = new ArrayList<>();
        for (LocalMedia localMedia : getCheckMediaList()) {
            checkMedia.add(localMedia.getPath());
        }
        return checkMedia;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            loadAllMedia();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_VIDEO_CODE) {
            loadAllMedia();
        }
    }

    private void onResult() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(DATA, getCheckMediaPathList());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void loadAllMedia() {
        LocalMediaLoader localMediaLoader = new LocalMediaLoader(this, mediaStoreConfig);
        localMediaLoader.setCompleteListener(this);
        localMediaLoader.loadAllMedia();
    }

    @Override
    public void loadComplete(List<LocalMediaFolder> folders) {
        mLocalMediaFolders = folders;
        List<LocalMedia> localMedia = null;
        for (LocalMediaFolder folder : folders) {
            if ("Camera".equals(folder.getName())) {
                localMedia = folder.getImages();
                checkLocalMediaSelect(localMedia);
                break;
            }
        }
        if (localMedia == null && folders.size() > 0) {
            localMedia = folders.get(0).getImages();
        }
        if (localMedia == null) {
            return;
        }
        localMedia.add(0, getCameraLocalMedia());
        mMediaStoreAdapter.setData(localMedia);
    }

    private LocalMedia getCameraLocalMedia() {
        LocalMedia localMedia = new LocalMedia();
        localMedia.setPath(CAMERA_PATH);
        return localMedia;
    }

    private void checkLocalMediaSelect(List<LocalMedia> localMediaList) {
        List<String> checkList = getCheckMediaPathList();
        if (checkList.size() > 0) {
            for (LocalMedia localMedia : localMediaList) {
                int index = checkList.indexOf(localMedia.getPath());
                if (index > -1) {
                    mCheckMediaList.set(index,localMedia);
                }
            }
        }
    }


    /**
     * 更新预览
     */
    public void notifyDataGalleryRecyclerView() {
        List<LocalMedia> localMediaList = getCheckMediaList();
        notifyDataSuccessButton(localMediaList.size());
        if (localMediaList.size() == 0) {
            mGalleryRecyclerView.setVisibility(View.GONE);
            return;
        }
        mGalleryRecyclerView.setVisibility(View.VISIBLE);
        mGalleryAdapter.setData(localMediaList);
        mGalleryAdapter.notifyDataSetChanged();
        mGalleryRecyclerView.scrollToPosition(mGalleryAdapter.getItemCount() - 1);
    }

    public void notifyDataSuccessButton(int size) {
        mediaStoreSuccessButton.setText(String.format("完成(%s/%s)", size, mediaStoreConfig.selectMaxCount));
    }

    /**
     * 显示 PopupWindow
     *
     * @param showAsView
     */
    private void showFolderPopupWindow(View showAsView) {
        View mView = LayoutInflater.from(context).inflate(R.layout.esay_md_fragment_folder_media_store, null, false);
        mView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        scaleAnimation.setDuration(250);
        mView.setAnimation(scaleAnimation);
        int height = getDelegate().findViewById(android.R.id.content).getHeight() - showAsView.getHeight();
        PopupWindow popupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT, height, true);
        popupWindow.showAsDropDown(showAsView, 0, 0);
    }

    private void startCameraVideoActivity() {
        Intent intent = new Intent(this, CameraVideoActivity.class);
        startActivityForResult(intent, CAMERA_VIDEO_CODE);
    }

    /**
     * 相册 adapter
     */
    public class MediaStoreAdapter extends RecyclerView.Adapter<MediaStoreAdapter.MediaViewHolder> {

        private List<LocalMedia> mLocalMediaList = new ArrayList<>();
        private Context mContext;

        public void setData(List<LocalMedia> localMediaList) {
            mLocalMediaList = localMediaList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            return new MediaViewHolder(View.inflate(mContext, R.layout.esay_md_item_media, null));
        }

        @Override
        public void onBindViewHolder(@NonNull final MediaViewHolder holder, int position) {
            final LocalMedia localMedia = mLocalMediaList.get(position);
            visibilityView(View.GONE, holder.iconChecked, holder.iconVideo, holder.videoTime, holder.audioView);

            if (getCheckMediaList().contains(localMedia)) {//是否选中
                holder.iconChecked.setVisibility(View.VISIBLE);
            }
            if (localMedia.getMimeType().equals(MediaStoreConfig.MIME_TYPE_VIDEO)) {
                visibilityView(View.VISIBLE, holder.iconVideo, holder.videoTime);
                holder.videoTime.setText(formatterTime(localMedia.getDuration()));
            }
            if (localMedia.getMimeType().equals(MediaStoreConfig.MIME_TYPE_AUDIO)) {
                String path = localMedia.getPath();
                String fileName = path.substring(path.lastIndexOf("/") + 1);
                visibilityView(View.VISIBLE, holder.audioView, holder.videoTime);
                holder.audioName.setText(fileName);
                holder.videoTime.setText(formatterTime(localMedia.getDuration()));
            }

            Glide.with(mContext).load(localMedia.getPath()).into(holder.imageView);
            if (!isCameraView(position, localMedia, holder)) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {//选中图片或者取消选择
                    @Override
                    public void onClick(View v) {
                        if (holder.iconChecked.isShown()) {
                            getCheckMediaList().remove(localMedia);
                            holder.iconChecked.setVisibility(View.GONE);
                        } else {
                            getCheckMediaList().add(localMedia);
                            holder.iconChecked.setVisibility(View.VISIBLE);
                        }
                        notifyDataGalleryRecyclerView();
                    }
                });
            }
        }

        private boolean isCameraView(int position, LocalMedia localMedia, MediaViewHolder holder) {
            holder.cameraView.setVisibility(View.GONE);
            if (position == 0 && CAMERA_PATH.equals(localMedia.getPath())) {
                holder.cameraView.setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCameraVideoActivity();
                    }
                });
                return true;
            }
            return false;
        }

        private void visibilityView(int visibility, View... views) {
            for (View view : views) {
                view.setVisibility(visibility);
            }
        }

        public String formatterTime(long duration) {
            SimpleDateFormat dateFormat;
            if (duration > 1000 * 60 * 60) {
                dateFormat = new SimpleDateFormat("hh:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            } else {
                dateFormat = new SimpleDateFormat("mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            }
            return dateFormat.format(new Date(duration));
        }


        @Override
        public int getItemCount() {
            return mLocalMediaList == null ? 0 : mLocalMediaList.size();
        }

        class MediaViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;

            public ImageView iconChecked;
            public ImageView iconVideo;
            public TextView videoTime;
            public View audioView, cameraView;
            public TextView audioName;

            public MediaViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.mediaImageView);
                iconChecked = itemView.findViewById(R.id.mediaCheckedImageView);
                iconVideo = itemView.findViewById(R.id.iconVideo);
                videoTime = itemView.findViewById(R.id.videoTime);
                audioView = itemView.findViewById(R.id.audioView);
                audioName = itemView.findViewById(R.id.audioName);
                cameraView = itemView.findViewById(R.id.cameraView);
            }
        }
    }
}
