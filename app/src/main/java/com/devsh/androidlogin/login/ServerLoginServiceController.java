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

package com.devsh.androidlogin.login;

import android.content.Context;
import android.util.Log;

import com.devsh.androidlogin.library.data.SharedData;
import com.devsh.androidlogin.utils.DeviceUtil;
import com.devsh.androidlogin.utils.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerLoginServiceController {

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
        String pushRegistrationToken = SharedData.getPushRegistrationToken(context);

        ServerLoginService service = ServiceGenerator.createService(sApiURL, ServerLoginService.class);

        ServerLoginRequest request = new ServerLoginRequest(provider, token, uid,
                userName, userEmail, userPhoto, pushRegistrationToken);

        Call<ServerLoginServiceResponse> call = service.login(request);

        call.enqueue(new Callback<ServerLoginServiceResponse>() {
            @Override
            public void onResponse(Call<ServerLoginServiceResponse> call, Response<ServerLoginServiceResponse> response) {
                if (response.isSuccessful()) {
                    ServerLoginServiceResponse body = response.body();
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
            public void onFailure(Call<ServerLoginServiceResponse> call, Throwable t) {
                Log.i(TAG, "error:");
                SharedData.clearSharedPreference(context);
                callback.onFail(t.getMessage());
            }
        });

    }
}
