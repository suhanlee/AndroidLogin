/*
 *
 *  Copyright (C) 2016 Suhan Lee
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.devsh.androidlogin.upload.gallery;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.devsh.androidlogin.R;

import java.util.List;


public class VideoGalleryAdapter extends RecyclerView.Adapter<VideoGalleryAdapter.CustomViewHolder> {
    private String TAG = "ItemGalleryAdapter";

    public static int RESULT_LOAD_IMAGE = 100;

    private List<VideoItem> mItemList;
    private Activity mActivity;

    public VideoGalleryAdapter(Activity context, List<VideoItem> feedItemList) {
        this.mItemList = feedItemList;
        this.mActivity = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.upload_list_row, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    private static String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        VideoItem feedItem = mItemList.get(i);

        // TODO : Plugitem 인 경우에 대한 처리가 필요

        String videoPath = feedItem.getThumbnailPath();
        if (videoPath != null) {
            //Download image using glide library
//            Glide
//                    .with(mActivity)
//                    .load(imagePath)
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .into(customViewHolder.videoView);

            VideoView videoView = customViewHolder.videoView;

            videoView.setVideoURI(Uri.parse(videoPath));
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            videoView.start();
        }

        if (feedItem.isPlusButton()) {
            customViewHolder.videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    mActivity.startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    /*
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
                */
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void addImageItem(String picturePath) {
        VideoItem item = new VideoItem();
        item.setThumbnail(picturePath);

        mItemList.add(0, item);
        notifyDataSetChanged();
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = mActivity.getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);

                if (cursor == null || cursor.getCount() < 1) {
                    return; // no cursor or no record. DO YOUR ERROR HANDLING
                }

                cursor.moveToFirst();

                do {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    if (columnIndex < 0) // no column index
                        return; // DO YOUR ERROR HANDLING

                    String picturePath = cursor.getString(columnIndex);

                    addImageItem(picturePath);
                    Log.i(TAG, "picturePath:" + picturePath);
                } while (cursor.moveToNext());
                cursor.close(); // close cursor
            }
        }
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        protected VideoView videoView;

        public CustomViewHolder(View view) {
            super(view);
            this.videoView = (VideoView) view.findViewById(R.id.videoView);
        }
    }
}