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

package com.devsh.androidlogin.gcm.network;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.util.Log;

import com.devsh.androidlogin.DeviceUtil;
import com.devsh.androidlogin.library.data.SharedData;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GCMRegistration {
    public static boolean DEBUG = true;

    private static Retrofit sRetrofit;
    private static GCMService service;
    private static String TAG = "GCMRegistration";

    public static void initialize(String baseUrl) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        sRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = sRetrofit.create(GCMService.class);
    }

    public static void registerToken(Context context, String registration_token, final GCMRegistrationCallback callback) {
        GCMRequest gcmRequest = new GCMRequest(
                SharedData.getServerToken(context),
                registration_token,
                DeviceUtil.getVersionName(context),
                DeviceUtil.getVersionCode(context));

        Call<GCMResponse> response = service.registerToken(gcmRequest);
        response.enqueue(new Callback<GCMResponse>() {
            @Override
            public void onResponse(Call<GCMResponse> call, Response<GCMResponse> response) {
                if (response.isSuccessful()) {
                    GCMResponse result = response.body();

                    if (DEBUG) {
                        Log.d(TAG, "response.isSuccess() == true");
                        printResponse(result);
                    }

                    callback.onResponseSuccess(result);
                }
            }

            @Override
            public void onFailure(Call<GCMResponse> call, Throwable t) {
                if (DEBUG) {
                    Log.d(TAG, "onFailure");
                }

                callback.onFailure(call, t);
            }
        });
    }

    private static void printResponse(GCMResponse result) {
        Log.i(TAG, "duplicate:" + result.getDuplicate());
    }
}
