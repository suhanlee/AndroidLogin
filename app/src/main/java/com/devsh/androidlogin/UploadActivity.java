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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devsh.androidlogin.upload.ProgressRequestBody;
import com.devsh.androidlogin.upload.ResourceUploadServiceResponse;
import com.devsh.androidlogin.upload.ResourceUploadServiceController;
import com.devsh.androidlogin.upload.gallery.VideoGalleryAdapter;
import com.devsh.androidlogin.upload.gallery.VideoItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends MainActivity {

    private RecyclerView recyclerview;
    private List<VideoItem> mItems;
    private VideoGalleryAdapter mAdapter;
    private EditText editTitle;
    private ProgressDialog progressDialog;
    private String TAG = "UploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initAdapterAndRecyclerView();

        editTitle = (EditText)findViewById(R.id.editTitle);

        Button btnAddPlus = (Button) findViewById(R.id.btnPlus);
        btnAddPlus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, VideoGalleryAdapter.RESULT_LOAD_IMAGE);
            }
        });

        Button btnUpload = (Button) findViewById(R.id.btnUpload);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                uploadFileFromGallery();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("파일 업로드");
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(true);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
    }


    private void uploadFileFromGallery() {
        String title = editTitle.getText().toString();

        for(VideoItem item : mItems) {
            if (item.isImage()) {
                ResourceUploadServiceController.uploadImageFile(getApplicationContext(), new File(item.getThumbnailPath()), title, new ProgressRequestBody.UploadCallbacks() {
                    @Override
                    public void onProgressUpdate(int percentage) {
                        progressDialog.setProgress(percentage);
                    }

                    @Override
                    public void onError() {
                        Log.e(TAG, "onError");
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFinish(ResourceUploadServiceResponse body) {
                        progressDialog.setProgress(100);
                        progressDialog.dismiss();
                        Toast.makeText(UploadActivity.this, body.getMovie_url(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.activityResult(requestCode, resultCode, data);
    }

    private void initAdapterAndRecyclerView() {
        // Initialize recycler view
        recyclerview = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerview.setHasFixedSize(true);

        mItems = new ArrayList<>();
        mAdapter = new VideoGalleryAdapter(UploadActivity.this, mItems);
        recyclerview.setAdapter(mAdapter);
    }

}
