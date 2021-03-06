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

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.devsh.androidlogin.common.Common;
import com.devsh.androidlogin.feed.FeedService;
import com.devsh.androidlogin.feed.model.Comment;
import com.devsh.androidlogin.feed.model.FeedItem;
import com.devsh.androidlogin.feedback.FeedbackServiceCallback;
import com.devsh.androidlogin.feedback.FeedbackServiceController;
import com.devsh.androidlogin.gcm.RedirectTo;
import com.devsh.androidlogin.library.data.SharedData;
import com.devsh.androidlogin.movie.CommentService;
import com.devsh.androidlogin.movie.CommentServiceResponse;
import com.devsh.androidlogin.movie.CommentsRecyclerAdapter;
import com.devsh.androidlogin.movie.ServerUpdateCallback;
import com.devsh.androidlogin.utils.ServiceGenerator;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieActivity extends Activity {
    private CommentsRecyclerAdapter adapter;

    private TextView txtTitle;
    private TextView txtTags;
    private TextView txtLike;

    private VideoView movieView;
    private RecyclerView recyclerView;
    private long mLastClickTime = 0;
    private RedirectTo redirectTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_movie);
        super.onCreate(savedInstanceState);

        getRedirectToDataFromIntent();
        initializeUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRedirectToDataFromIntent();
        getMovieDataFromServer(redirectTo.getMovieId());
    }

    private void getRedirectToDataFromIntent() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if(extras.containsKey(Common.REDIRECT_TO_KEY)) {
                String rt = extras.getString(Common.REDIRECT_TO_KEY);
                Gson gson = new Gson();
                redirectTo = gson.fromJson(rt, RedirectTo.class);
            }
        }
    }

    private void initializeUI() {
        txtTitle = (TextView) findViewById(R.id.txt_title);

        txtTags = (TextView) findViewById(R.id.txt_tags);

        txtLike = (TextView) findViewById(R.id.txt_like);

        Button likeDislikeButton = (Button) findViewById(R.id.like_dislike_button);
        likeDislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackServiceController.likeOrDislike(getApplicationContext(),
                        redirectTo.getMovieId(), new FeedbackServiceCallback() {
                            @Override
                            public void onSuccess(String likeCount) {
                                Toast.makeText(getApplicationContext(), "Like count:" + likeCount, Toast.LENGTH_LONG).show();
                                txtLike.setText(likeCount);
                            }
                        });
            }
        });

        final EditText editComment = (EditText) findViewById(R.id.edit_comment);

        movieView = (VideoView) findViewById(R.id.movie_view);
        recyclerView = (RecyclerView) findViewById(R.id.comments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        adapter = new CommentsRecyclerAdapter(MovieActivity.this, redirectTo.getMovieId(), new ArrayList<Comment>());
        adapter.setUpdateCallback(new ServerUpdateCallback() {
                                      @Override
                                      public void onSuccess() {
                                        getMovieDataFromServer(redirectTo.getMovieId());
                                      }
                                  });


        recyclerView.setAdapter(adapter);

        Button btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }

                mLastClickTime = SystemClock.elapsedRealtime();

                postComment(editComment.getText().toString());
            }
        });
    }

    private void postComment(String message) {
        CommentService service = ServiceGenerator.createService(Common.API_BASE_URL, CommentService.class);

        Call<CommentServiceResponse> call = service.postComment(SharedData.getServerToken(getApplicationContext()), redirectTo.getMovieId(), message);
        call.enqueue(new Callback<CommentServiceResponse>() {

            @Override
            public void onResponse(Call<CommentServiceResponse> call, Response<CommentServiceResponse> response) {
                CommentServiceResponse body = response.body();
                if (response.isSuccessful()) {
                    if (body.getSuccess()) {
                        Toast.makeText(getApplicationContext(), "Post comment", Toast.LENGTH_LONG).show();
                        getMovieDataFromServer(redirectTo.getMovieId());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error: Post Comment ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CommentServiceResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getMovieDataFromServer(String movieId) {
        FeedService service = ServiceGenerator.createService(Common.API_BASE_URL, FeedService.class);

        Call<FeedItem> call = service.getFeedItems(movieId, SharedData.getServerToken(getApplicationContext()));
        call.enqueue(new Callback<FeedItem>() {
            @Override
            public void onResponse(Call<FeedItem> call, Response<FeedItem> response) {
                if (response.isSuccessful()) {
                    FeedItem feedItem = response.body();
                    updateMovieDetail(feedItem);
                }
            }

            @Override
            public void onFailure(Call<FeedItem> call, Throwable t) {

            }
        });
    }

    private void updateMovieDetail(FeedItem feedItem) {

        if (!movieView.isPlaying()) {
            movieView.setVideoURI(Uri.parse(feedItem.getMovie_url()));
            movieView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            movieView.start();
        }

        txtTitle.setText(feedItem.getTitle());
        txtTags.setText(feedItem.getTags());
        txtLike.setText(feedItem.getFeedback().getLike());
        adapter.setCommentList(feedItem.getComments());
    }
}
