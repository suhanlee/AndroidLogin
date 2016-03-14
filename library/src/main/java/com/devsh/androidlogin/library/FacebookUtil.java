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
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.List;

public class FacebookUtil {
    private static FacebookUtil sInstance;
    private static AccessTokenTracker accessTokenTracker;
    private static ProfileTracker profileTracker;
    private static Context sContext;

    private static CallbackManager callbackManager;
    private String TAG = "FacebookUtil";
    private Callback loginCallback;
    private Callback logoutCallback;
    private Callback updateTokenCallback;

    interface Callback {
        void onCallback(AccessToken currentToken);
    }

    public static FacebookUtil getInstance() {
        if (sInstance == null) {
            sInstance = new FacebookUtil();
        }
        return sInstance;
    }

    private Callback nullCallback = new Callback() {
        @Override
        public void onCallback(AccessToken currentToken) {

        }
    };

    private FacebookUtil() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                printLoginResult(loginResult);
            }

            @Override
            public void onCancel() {
                AccessToken token = AccessToken.getCurrentAccessToken();
                if (token == null) {
                    Log.e(TAG, "token is null");
                } else {
                    Log.e(TAG, AccessToken.getCurrentAccessToken() + "");
                }
            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (oldAccessToken == null && currentAccessToken != null) {
                    Log.e(TAG, "login");
                    Log.e(TAG, "loginResult currentToken" + currentAccessToken.getToken());
                    loginCallback.onCallback(currentAccessToken);

                }

                if (oldAccessToken != null && currentAccessToken == null) {
                    Log.e(TAG, "logout");
                    Log.e(TAG, "loginResult oldAccessToken" + oldAccessToken.getToken());
                    logoutCallback.onCallback(currentAccessToken);
                }

                if (oldAccessToken != null && currentAccessToken != null) {
                    updateTokenCallback.onCallback(currentAccessToken);
                }
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                Log.i(TAG, "oldProfile" + oldProfile + "currentProfile" + currentProfile);
            }
        };

        loginCallback = nullCallback;
        logoutCallback = nullCallback;
        updateTokenCallback = nullCallback;
    }

    public void setLoginCallback(Callback callback) {
        loginCallback = callback;
    }

    public void setLogoutCallback(Callback callback) {
        logoutCallback = callback;
    }

    public void setUpdateTokenCallback(Callback callback) {
        updateTokenCallback = callback;
    }

    private void printLoginResult(LoginResult loginResult) {
        Log.i(TAG, loginResult + "");
        Log.e(TAG, "loginResult getAccessToken()" + loginResult.getAccessToken().getToken());
        Log.e(TAG, "loginResult getUserId()" + loginResult.getAccessToken().getUserId());
        Log.e(TAG, "loginResult getRecentlyGrantedPermissions" + loginResult.getRecentlyGrantedPermissions());
    }

    public void logIn(Activity activity, List<String> permissionList) {
        LoginManager.getInstance().logInWithReadPermissions(activity, permissionList);
    }

    public void logOut() {
        LoginManager.getInstance().logOut();
    }

    public boolean isLogined() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            return true;
        }
        return false;
    }

    public String getUserId() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            accessToken.getUserId();
        }

        return null;
    }

    public void getProfile() {
        Profile profile = Profile.getCurrentProfile();
        printProfile(profile);
    }

    private void printProfile(Profile profile) {
        Log.e(TAG, "firstName" + profile.getFirstName());
        Log.e(TAG, "id" + profile.getId());
        Log.e(TAG, "lastName" + profile.getLastName());
        Log.e(TAG, "name" + profile.getName());
        Log.e(TAG, "uri" + profile.getLinkUri());
        Log.e(TAG, "picture" + profile.getProfilePictureUri(200, 200));
    }

    private void checkLoginToken() {
        // If the access token is available already assign it.
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            Log.e(TAG, "accessToken" + accessToken.getToken());
            Log.e(TAG, "userId" + accessToken.getUserId());
        } else {
            Log.e(TAG, "accessToken is null");
        }
    }

    public static void onResume() {
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(sContext);
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static void initialize(Context context) {
        sContext = context.getApplicationContext();
        FacebookSdk.sdkInitialize(sContext);
    }

    public static void destroy() {
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    public static void onPause() {
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.deactivateApp(sContext);
    }
}
