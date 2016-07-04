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

package com.devsh.androidlogin.server;

import android.content.Context;
import android.util.Log;

import com.devsh.androidlogin.library.data.SharedData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerLogin {

    private static String TAG = "ServerLogin";
    private static String sApiURL;

    public static void initialize(String apiURL) {
        sApiURL = apiURL;
    }

    public static void login(final Context context, final ServerLoginResultCallback callback) {
        String provider = SharedData.getAccountProvider(context);
        String uid = SharedData.getAccountId(context);
        String token = SharedData.getAccountIdToken(context);
        String userName = SharedData.getAccountUserName(context);
        String userEmail = SharedData.getAccountUserEmail(context);
        String userPhoto = SharedData.getAccountUserPhoto(context);

        ServerLoginService service = ServiceGenerator.createService(sApiURL, ServerLoginService.class);

        Call<ServerLoginResponse> call = service.login(provider, token, uid, userName, userEmail, userPhoto);
        call.enqueue(new Callback<ServerLoginResponse>() {
            @Override
            public void onResponse(Call<ServerLoginResponse> call, Response<ServerLoginResponse> response) {
                if (response.isSuccessful()) {
                    ServerLoginResponse body = response.body();
                    if (body.success) {
                        SharedData.setLoggedIn(context, true);
                        callback.onSuccess(body.api_token);
                    } else {
                        SharedData.setLoggedIn(context, false);
                        SharedData.clearSharedPreference(context);
                        callback.onFail(body.error_message);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerLoginResponse> call, Throwable t) {
                Log.i(TAG, "error:");
                callback.onFail(t.getMessage());
            }
        });

    }
}
