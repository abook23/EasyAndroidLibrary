package com.android.easy.mediastore;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.easy.mediastore.R;
import com.android.easy.mediastore.utils.LocalMedia;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * author abook23@163.com
 * date 2020/04/30
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHold> {

    private Context mContext;
    List<LocalMedia> mLocalMediaList;

    public void setData(List<LocalMedia> localMediaList) {
        mLocalMediaList = localMediaList;
//        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GalleryViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new GalleryViewHold(View.inflate(mContext, R.layout.item_media_gallery, null));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHold holder, int position) {

        LocalMedia localMedia = mLocalMediaList.get(position);
        final String path = mLocalMediaList.get(position).getPath();
        Glide.with(mContext).load(path).into(holder.mImageView);
        visibilityView(View.GONE, holder.iconVideo);
        if (localMedia.getMimeType().equals(MediaStoreConfig.MIME_TYPE_VIDEO)) {
            visibilityView(View.VISIBLE, holder.iconVideo);
            holder.iconVideo.setImageResource(R.mipmap.video);
        }
        if (localMedia.getMimeType().equals(MediaStoreConfig.MIME_TYPE_AUDIO)) {
            visibilityView(View.VISIBLE, holder.iconVideo);
            holder.iconVideo.setImageResource(R.mipmap.audio);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list = MediaStoreActivity.getCheckMediaPathList();
                int position = list.indexOf(path);
                MediaStoreInfoActivity.start(mContext, position, list);
            }
        });
    }


    private void visibilityView(int visibility, View... views) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    @Override
    public int getItemCount() {
        return mLocalMediaList == null ? 0 : mLocalMediaList.size();
    }

    class GalleryViewHold extends RecyclerView.ViewHolder {

        ImageView mImageView;
        ImageView iconVideo;

        public GalleryViewHold(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.mediaImageView);
            iconVideo = itemView.findViewById(R.id.iconVideo);
        }
    }
}
