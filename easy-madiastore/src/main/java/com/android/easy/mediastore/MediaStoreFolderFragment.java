package com.android.easy.mediastore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.easy.mediastore.utils.LocalMediaFolder;
import com.bumptech.glide.Glide;

import java.util.List;


public class MediaStoreFolderFragment extends Fragment {
    public List<LocalMediaFolder> mLocalMediaFolders;

    private OnSelectFolderListener mOnSelectFolderListener;
    private int checkPosition = 0;

    public MediaStoreFolderFragment() {
        // Required empty public constructor
    }

    public static MediaStoreFolderFragment newInstance(List<LocalMediaFolder> list, OnSelectFolderListener onSelectFolderListener) {
        MediaStoreFolderFragment fragment = new MediaStoreFolderFragment();
        fragment.mLocalMediaFolders = list;
        fragment.mOnSelectFolderListener = onSelectFolderListener;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       return inflater.inflate(R.layout.esay_md_fragment_folder_media_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Adapter adapter = new Adapter(mLocalMediaFolders, mOnSelectFolderListener);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }


    public void setOnSelectFolderListener(OnSelectFolderListener onSelectFolderListener) {
        mOnSelectFolderListener = onSelectFolderListener;
    }

    public interface OnSelectFolderListener {
        void onSelectFolder(LocalMediaFolder localMediaFolder);
    }

    public class Adapter extends RecyclerView.Adapter<FolderViewHolder> {
        List<LocalMediaFolder> mLocalMediaFolders;
        OnSelectFolderListener mOnSelectFolderListener;

        public Adapter(List<LocalMediaFolder> localMediaFolders, OnSelectFolderListener onSelectFolderListener) {
            mLocalMediaFolders = localMediaFolders;
            mOnSelectFolderListener = onSelectFolderListener;
        }

        @NonNull
        @Override
        public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FolderViewHolder(LayoutInflater.from(getContext()).inflate( R.layout.esay_md_item_folder, parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull FolderViewHolder holder, final int position) {
            final LocalMediaFolder localMediaFolder = mLocalMediaFolders.get(position);
            Glide.with(getContext()).load(localMediaFolder.getFirstImagePath()).into(holder.imageView);
            holder.textView1.setText(localMediaFolder.getName());
            holder.textView2.setText(localMediaFolder.getImageNum() + "张");
            if (checkPosition == position){
                holder.checkbox.setVisibility(View.VISIBLE);
            }else {
                holder.checkbox.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnSelectFolderListener.onSelectFolder(localMediaFolder);
                    checkPosition = position;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mLocalMediaFolders == null ? 0 : mLocalMediaFolders.size();
        }

    }

    private class FolderViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView1;
        public TextView textView2;
        public ImageView checkbox;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView1 = itemView.findViewById(R.id.text1);
            textView2 = itemView.findViewById(R.id.text2);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}
