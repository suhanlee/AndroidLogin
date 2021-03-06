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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.devsh.androidlogin.common.Common;
import com.devsh.androidlogin.R;
import com.devsh.androidlogin.feed.model.Comment;
import com.devsh.androidlogin.library.data.SharedData;
import com.devsh.androidlogin.utils.ServiceGenerator;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.CustomViewHolder> {
    private final String movieId;
    private List<Comment> commentList;

    private Activity activity;
    private ServerUpdateCallback serverUpdateCallback;

    public CommentsRecyclerAdapter(Activity activity, String movieId, List<Comment> commentList) {
        this.movieId = movieId;
        this.activity = activity;
        this.commentList = commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
        notifyDataSetChanged();
    }

    public void setUpdateCallback(ServerUpdateCallback callback) {
        this.serverUpdateCallback = callback;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_comments_list_row, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        final Comment comment = commentList.get(i);

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

        // Delete Button
        if (comment.getOwner()) {
            customViewHolder.deleteButton.setVisibility(View.VISIBLE);
            customViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteComment(comment.getId());
                }
            });
        } else {
            customViewHolder.deleteButton.setVisibility(View.INVISIBLE);
        }
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
        protected Button deleteButton;

        public CustomViewHolder(View view) {
            super(view);
            this.authorName = (TextView) view.findViewById(R.id.author_name);
            this.authorImage = (ImageView) view.findViewById(R.id.author_image);
            this.authorMessage = (TextView) view.findViewById(R.id.author_message);
            this.deleteButton = (Button) view.findViewById(R.id.btn_delete_comment);
        }
    }


    private void deleteComment(String commentId) {
        CommentService service = ServiceGenerator.createService(Common.API_BASE_URL, CommentService.class);

        Call<CommentServiceResponse> call = service.deleteComment(commentId, SharedData.getServerToken(activity));
        call.enqueue(new Callback<CommentServiceResponse>() {
            @Override
            public void onResponse(Call<CommentServiceResponse> call, Response<CommentServiceResponse> response) {
                if (response.isSuccessful()) {
                    CommentServiceResponse body = response.body();
                    if (body.getSuccess()) {
                        Toast.makeText(activity, "Delete Comment", Toast.LENGTH_LONG).show();
                        if (serverUpdateCallback != null) {
                            serverUpdateCallback.onSuccess();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentServiceResponse> call, Throwable t) {

            }
        });
    }


}