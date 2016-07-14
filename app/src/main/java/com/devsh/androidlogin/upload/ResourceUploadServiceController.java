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

package com.devsh.androidlogin.upload;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;

import com.devsh.androidlogin.common.Common;
import com.devsh.androidlogin.library.data.SharedData;
import com.devsh.androidlogin.utils.ServiceGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResourceUploadServiceController {

    private static String TAG = "UploadStorage";

    public static void uploadImageFile(Context context, File file, String titleValue, String tagsValue, final ProgressRequestBody.UploadCallbacks uploadCallback) {
        ResourceUploadService service = ServiceGenerator.createService(Common.API_BASE_URL, ResourceUploadService.class);
        Map<String, RequestBody> map = new HashMap<>();

        RequestBody api_key = RequestBody.create(MediaType.parse("text/plain"), SharedData.getServerToken(context));
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), titleValue);
        RequestBody tags = RequestBody.create(MediaType.parse("text/plain"), tagsValue);
        RequestBody upload = new ProgressRequestBody(file, uploadCallback);

        map.put("api_token", api_key);
        map.put("movie[title]", title);
        map.put("movie[tags]", tags);

        map.put("movie[upload]" + "\"; filename=" + file.getName().trim(), upload);

        Call<ResourceUploadServiceResponse> call = service.uploadImage(map);
        call.enqueue(new Callback<ResourceUploadServiceResponse>() {
            @Override
            public void onResponse(Call<ResourceUploadServiceResponse> call, Response<ResourceUploadServiceResponse> response) {
                if (response.isSuccessful()) {
                    ResourceUploadServiceResponse body = response.body();
                    Log.i(TAG, "resource:" + body.getMovie_url());
                    uploadCallback.onFinish(body);
                }
            }

            @Override
            public void onFailure(Call<ResourceUploadServiceResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure:" + t.getMessage());
                    uploadCallback.onError();
            }
        });
    }

    private static String getMimeType(File file) {
        String mimeType = "";

        if (file.getName().endsWith(".png")) {
            mimeType = "image/png";
        } else if (file.getName().endsWith(".jpg")){
            mimeType = "image/jpeg";
        } else if (file.getName().endsWith(".mp4")) {
            mimeType = "video/mp4";
        }

        return mimeType;
    }
}
