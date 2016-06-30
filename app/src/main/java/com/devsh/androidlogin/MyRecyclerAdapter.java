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

package com.devsh.androidlogin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.devsh.androidlogin.feed.model.FeedItem;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {
    private List<FeedItem> feedItemList;

    private Context context;

    public MyRecyclerAdapter(Context context, List<FeedItem> feedItemList) {
        this.context = context;
        this.feedItemList = feedItemList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);

        // Author Name
        customViewHolder.authorName.setText(feedItem.getAuthor().getName());

        // Author Image
        Glide
                .with(context)
                .load(feedItem.getAuthor().getImage_url())
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(customViewHolder.authorImage);

        // Movie Image
        Glide
                .with(context)
                .load(feedItem.getThumb_movie_url())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(customViewHolder.imageView);

        //Setting text view title
        customViewHolder.textView.setText(Html.fromHtml(feedItem.getTitle()));

        // Feedback
        customViewHolder.feedbackLike.setText(feedItem.getFeedback().getLike());
        customViewHolder.feedbackDislike.setText(feedItem.getFeedback().getDislike());
        customViewHolder.commentsCount.setText(feedItem.getComments().size()+"");
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView authorName;
        protected ImageView authorImage;

        protected ImageView imageView;
        protected TextView textView;

        protected TextView feedbackLike;
        protected TextView feedbackDislike;

        protected TextView commentsCount;

        public CustomViewHolder(View view) {
            super(view);
            this.authorName = (TextView) view.findViewById(R.id.author_name);
            this.authorImage = (ImageView) view.findViewById(R.id.author_image);

            this.imageView = (ImageView) view.findViewById(R.id.videoView);
            this.textView = (TextView) view.findViewById(R.id.title);

            this.feedbackLike = (TextView) view.findViewById(R.id.like_count);
            this.feedbackDislike = (TextView) view.findViewById(R.id.dislike_count);
            this.commentsCount = (TextView) view.findViewById(R.id.comments_count);
        }
    }
}