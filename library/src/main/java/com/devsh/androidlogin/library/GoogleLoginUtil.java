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

package com.devsh.androidlogin.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.devsh.androidlogin.library.callback.GoogleLoginInResultCallback;
import com.devsh.androidlogin.library.data.SharedData;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class GoogleLoginUtil {
    private final static String TAG = "GoogleSignInUtil";
    private final static int RC_SIGN_IN = 100;
    private static GoogleSignInOptions gso;

    private static GoogleLoginUtil sInstance;
    private static String sServerClientID;
    private GoogleApiClient googleApiClient;
    private Context context;
    private GoogleLoginInResultCallback loginResultCallback;
    private ResultCallback<Status> logoutResultCallback;

    public static GoogleLoginUtil getInstance() {
        if (sInstance == null) {
            sInstance = new GoogleLoginUtil();
        }
        return sInstance;
    }

    public static void setServerClientID(String serverClientID) {
        sServerClientID = serverClientID;
    }

    private GoogleLoginUtil() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken(sServerClientID)
                .build();
    }

    public void initialize(FragmentActivity activity) {
        // TODO: sServerClientID 가 설정되어 있지 않다면 예외 발생
        context = activity;
        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e(TAG, "onConnectionFailed: " + connectionResult);
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.i(TAG, "onConnected");
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.i(TAG, "onConnectionSuspended");
            }
        });

        googleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.i(TAG, "onConnectionFailed");
            }
        });
    }

    public boolean isConnected() {
        boolean isConnected = googleApiClient.isConnected();

        Log.i(TAG, "isConnected()" + isConnected);
        return isConnected;
    }

    public boolean isSignedIn(Context context) {
        return SharedData.getAccountId(context) != null
                && SharedData.getAccountIdToken(context) != null;
    }

    public void signIn(Activity activity) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (logoutResultCallback != null) {
                            logoutResultCallback.onResult(status);
                        }
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            Log.i(TAG, "Signed in");
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String userName = acct.getDisplayName();
            String userEmail = acct.getEmail();
            String userId = acct.getId();
            Uri userPhoto = acct.getPhotoUrl();
            String userIdToken = acct.getIdToken();

            SharedData.putAccountProvider(context, SharedData.PROVIDER_GOOGLE);
            SharedData.putAccountIdToken(context, userIdToken);
            SharedData.putAccountId(context, userId);
            SharedData.putAccountUserName(context, userName);
            SharedData.putAccountUserEmail(context, userEmail);

            if (userPhoto != null) {
                SharedData.putAccountUserPhoto(context, userPhoto.toString());
            }

            if (loginResultCallback != null) {
                loginResultCallback.onSuccess(result);
            }
        } else {
            Log.i(TAG, "Signed out");

            if (loginResultCallback != null) {
                loginResultCallback.onFail(result);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void setLoginResultCallback(GoogleLoginInResultCallback callback) {
        loginResultCallback = callback;
    }

    public void setLogoutResultCallback(ResultCallback<Status> callback) {
        logoutResultCallback = callback;
    }
}
