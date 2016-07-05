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
import android.os.Bundle;
import android.util.Log;

import com.devsh.androidlogin.library.data.SharedData;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FacebookLoginUtil {
    private static FacebookLoginUtil sInstance;
    private static AccessTokenTracker accessTokenTracker;
    private static ProfileTracker profileTracker;
    private static Context sContext;

    private static CallbackManager callbackManager;
    private static FacebookCallback<LoginResult> loginResultCallback;

    private String TAG = "FacebookUtil";
    private Callback loginCallback;
    private Callback logoutCallback;
    private Callback updateTokenCallback;

    public interface Callback {
        void onCallback(AccessToken currentToken);
    }

    public static synchronized FacebookLoginUtil getInstance() {
        if (sInstance == null) {
            sInstance = new FacebookLoginUtil();
        }
        return sInstance;
    }

    private Callback nullCallback = new Callback() {
        @Override
        public void onCallback(AccessToken currentToken) {

        }
    };

    private FacebookLoginUtil() {
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (oldAccessToken == null && currentAccessToken != null) {
                    loginCallback.onCallback(currentAccessToken);
                }

                if (oldAccessToken != null && currentAccessToken == null) {
                    SharedData.clearSharedPreference(sContext);
                    logoutCallback.onCallback(currentAccessToken);
                }

                if (oldAccessToken != null && currentAccessToken != null) {
                    updateTokenCallback.onCallback(currentAccessToken);
                }
            }
        };

//        profileTracker = new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(
//                    Profile oldProfile,
//                    Profile currentProfile) {
//                Log.i(TAG, "oldProfile" + oldProfile + "currentProfile" + currentProfile);
//            }
//        };

        loginCallback = nullCallback;
        logoutCallback = nullCallback;
        updateTokenCallback = nullCallback;

        // LoginResultCallback
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();
                final AccessToken accessToken = loginResult.getAccessToken();
                if (profile != null) {
                    SharedData.putAccountProvider(sContext, SharedData.PROVIDER_FACEBOOK);
                    SharedData.putAccountIdToken(sContext, accessToken.getToken());
                    SharedData.putAccountId(sContext, profile.getId());
                    SharedData.putAccountUserName(sContext, profile.getName());
                    SharedData.putAccountUserPhoto(sContext, profile.getProfilePictureUri(200, 200).toString());
                    // No email

                    loginResultCallback.onSuccess(loginResult);
                } else {
                    final LoginResult loginResult2 = loginResult;
                    GraphRequest request = GraphRequest.newMeRequest(
                            accessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {

                                    SharedData.putAccountProvider(sContext, SharedData.PROVIDER_FACEBOOK);
                                    SharedData.putAccountIdToken(sContext, accessToken.getToken());
                                    SharedData.putAccountId(sContext, accessToken.getUserId());

                                    try {
                                        String userName = response.getJSONObject().getString("name");
                                        String userPhoto = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                        String userEmail = object.getString("email");
                                        SharedData.putAccountUserName(sContext, userName);
                                        SharedData.putAccountUserPhoto(sContext, userPhoto);
                                        SharedData.putAccountUserEmail(sContext, userEmail);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    loginResultCallback.onSuccess(loginResult2);
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,picture");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }

            @Override
            public void onCancel() {
                SharedData.clearSharedPreference(sContext);
                loginResultCallback.onCancel();
            }

            @Override
            public void onError(FacebookException error) {
                SharedData.clearSharedPreference(sContext);
                loginResultCallback.onError(error);
            }
        });

    }

    public void setLoginResultCallback(FacebookCallback<LoginResult> callback) {
        loginResultCallback = callback;
    }

    public void setLogoutResultCallback(Callback callback) {
        setLogoutCallbackByAccessToken(callback);
    }

    // access token
    public void setLoginCallbackByAccessToken(Callback callback) {
        loginCallback = callback;
    }

    public void setLogoutCallbackByAccessToken(Callback callback) {
        logoutCallback = callback;
    }

    public void setUpdateTokenCallbackByAccessToken(Callback callback) {
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

    public void logout() {
        LoginManager.getInstance().logOut();
    }

    public boolean isLogined() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            return true;
        }

        return false;
    }

    public static void initialize(Context context) {
        sContext = context.getApplicationContext();
        FacebookSdk.sdkInitialize(sContext);
    }

    public void onPause() {
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.deactivateApp(sContext);
    }

    public void onResume() {
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(sContext);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onDestroy() {
        if (accessTokenTracker != null) {
            accessTokenTracker.stopTracking();
        }

        if (profileTracker != null) {
            profileTracker.stopTracking();
        }
    }
}
