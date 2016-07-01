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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.devsh.androidlogin.feed.model.FeedItem;
import com.devsh.androidlogin.movie.CommentsRecyclerAdapter;
import com.google.gson.Gson;

public class MovieActivity extends Activity {
    private FeedItem feedItem;
    private CommentsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_movie);
        super.onCreate(savedInstanceState);

        String temp = getIntent().getExtras().getString(FeedRecyclerAdapter.FEED_ITEM_KEY);
        Gson gson = new Gson();
        feedItem = gson.fromJson(temp, FeedItem.class);

        initializeUI();
    }

    private void initializeUI() {
        TextView txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(feedItem.getTitle());

        VideoView movieView = (VideoView) findViewById(R.id.movie_view);
        movieView.setVideoURI(Uri.parse(feedItem.getMovie_url()));
        movieView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        movieView.start();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comments_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        adapter = new CommentsRecyclerAdapter(MovieActivity.this, feedItem.getComments());
        recyclerView.setAdapter(adapter);

        Button btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
