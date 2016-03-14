/*
 * Copyright (C) 2015 Suhan Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.devsh.androidlogin.library;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class GoogleSignInUtil {
    private final static String TAG = "GoogleSignInUtil";
    private final static String SERVER_CLIENT_ID = "486150556496-2h0adv5kgeesn7s6303ri6kbn6cncpu5.apps.googleusercontent.com";
    private final static int RC_SIGN_IN = 100;
    private final GoogleSignInOptions gso;

    private static GoogleSignInUtil sInstance;
    private GoogleApiClient googleApiClient;

    public static GoogleSignInUtil getInstance() {
        if (sInstance == null) {
            sInstance = new GoogleSignInUtil();
        }
        return sInstance;
    }

    private GoogleSignInUtil() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken(SERVER_CLIENT_ID)
                .build();


    }

    public void initialize(FragmentActivity activity) {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

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

    public void isConnected() {
        Log.i(TAG, "isConnected()" + googleApiClient.isConnected());
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
                        Log.i(TAG, "getStatus():" + status.getStatus());
                        Log.i(TAG, "statusCode:" + status.getStatusCode());
                        Log.i(TAG, "statusMessage:" + status.getStatusMessage());
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            Log.i(TAG, "Signed in");
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            String idToken = acct.getIdToken();
            Log.i(TAG, "signInResult personName:" + personName);
            Log.i(TAG, "signInResult personEmail:" + personEmail);
            Log.i(TAG, "signInResult personId:" + personId);
            Log.i(TAG, "signInResult idToken:" + idToken);
            Log.i(TAG, "signInResult personPhoto:" + personPhoto);
        } else {
            Log.i(TAG, "Signed out");
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
}
