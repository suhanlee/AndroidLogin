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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.devsh.androidlogin.feed.FeedService;
import com.devsh.androidlogin.feed.model.Feed;
import com.devsh.androidlogin.feed.model.FeedItem;
import com.devsh.androidlogin.library.AndroidLogin;
import com.devsh.androidlogin.library.data.SharedData;
import com.devsh.androidlogin.library.server.ServiceGenerator;
import com.facebook.login.LoginManager;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;

    private SwipeRefreshLayout swipeLayout;
    private FeedService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        initializeNavigationView();

        // Initialize service
        service = ServiceGenerator.createService(Common.API_BASE_URL, FeedService.class);

        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeed();
            }
        });

        updateFeed();

    }

    private void initializeNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);


        String userPhotoUrl = SharedData.getAccountUserPhoto(getApplicationContext());
        ImageView profilePhotoView = (ImageView) headerView.findViewById(R.id.imgProfileIcon);

        if (userPhotoUrl != null) {
            // Author Image
            Glide
                    .with(getApplicationContext())
                    .load(SharedData.getAccountUserPhoto(getApplicationContext()))
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(profilePhotoView);
        }

        String userProfileName = SharedData.getAccountUserName(getApplicationContext());

        TextView profileNameView = (TextView) headerView.findViewById(R.id.txtProfileName);
        if (userProfileName != null) {
            profileNameView.setText(userProfileName);
        }

        String userProfileEmail = SharedData.getAccountUserEmail(getApplicationContext());
        TextView profileEmailView = (TextView) headerView.findViewById(R.id.txtProfileEmail);
        if (userProfileEmail != null) {
            profileEmailView.setText(userProfileEmail);
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_make) {
            // Handle the camera action
        } else if (id == R.id.nav_feed) {

        } else if (id == R.id.nav_upload) {

        } else if (id == R.id.nav_logout) {
            AndroidLogin.logout();
            goToStartActivity();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void goToStartActivity() {
        Intent intent = new Intent(FeedActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateFeed() {
        Call<Feed> call = service.getFeeds();
        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                if (response.isSuccessful()) {
                    Feed feed = response.body();
                    List<FeedItem> items = feed.getFeed_list();

                    adapter = new MyRecyclerAdapter(FeedActivity.this, items);
                    mRecyclerView.setAdapter(adapter);
                    swipeLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                swipeLayout.setRefreshing(false);

            }
        });
    }
}
