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

package com.devsh.androidlogin.feedback;

import android.content.Context;
import android.widget.Toast;

import com.devsh.androidlogin.Common;
import com.devsh.androidlogin.library.data.SharedData;
import com.devsh.androidlogin.server.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackServiceController {

    public static void likeOrDislike(final Context context, String movieId) {
        FeedbackService service = ServiceGenerator.createService(Common.API_BASE_URL, FeedbackService.class);
        String apiToken = SharedData.getServerToken(context);
        FeedbackServiceRequest request = new FeedbackServiceRequest(apiToken, movieId);

        Call<FeedbackServiceResponse> call = service.likeOrDislike(request);
        call.enqueue(new Callback<FeedbackServiceResponse>() {

            @Override
            public void onResponse(Call<FeedbackServiceResponse> call, Response<FeedbackServiceResponse> response) {
                FeedbackServiceResponse body = response.body();
                if (response.isSuccessful()) {
                    if (body.isSuccess()) {
                        Toast.makeText(context, "Like count:" + body.getLikeCount(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Error: Like Action" + body.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FeedbackServiceResponse> call, Throwable t) {
                Toast.makeText(context, "fail", Toast.LENGTH_LONG).show();
            }
        });
    }
}
