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

package com.devsh.androidlogin.movie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.devsh.androidlogin.MovieActivity;
import com.devsh.androidlogin.R;
import com.devsh.androidlogin.feed.model.Comment;
import com.devsh.androidlogin.feed.model.FeedItem;
import com.google.gson.Gson;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.CustomViewHolder> {
    private final List<Comment> commentList;

    private Activity activity;

    public CommentsRecyclerAdapter(Activity activity, List<Comment> commentList) {
        this.activity = activity;
        this.commentList = commentList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_list_row, viewGroup, false);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_comments_list_row, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        Comment comment = commentList.get(i);

        // Author Name
        customViewHolder.authorName.setText(comment.getUsername());

        // Author Message
        customViewHolder.authorMessage.setText(comment.getUser_message());

        // Author Image
        Glide
                .with(activity)
                .load(comment.getUser_profile_image_url())
                .bitmapTransform(new CropCircleTransformation(activity))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(customViewHolder.authorImage);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView authorName;
        protected TextView authorMessage;

        protected ImageView authorImage;

        public CustomViewHolder(View view) {
            super(view);
            this.authorName = (TextView) view.findViewById(R.id.author_name);
            this.authorImage = (ImageView) view.findViewById(R.id.author_image);
            this.authorMessage = (TextView) view.findViewById(R.id.author_message);
        }
    }
}