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
import android.support.v4.app.FragmentActivity;

import com.devsh.androidlogin.library.callback.GoogleLoginInResultCallback;
import com.devsh.androidlogin.library.data.SharedData;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.List;

public class AndroidLogin {

    private static Context sContext;

    public enum LoginMethod {
        Facebook,
        Google
    }

    private static LoginMethod loginSelected;

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (loginSelected) {
            case Google:
                GoogleLoginUtil.getInstance().onActivityResult(requestCode, resultCode, data);
                break;
            case Facebook:
                FacebookLoginUtil.getInstance().onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public static void initialzie(FragmentActivity context, String serverClientID) {
        GoogleLoginUtil.setServerClientID(serverClientID);
        initialzie(context);
    }

    public static void initialzie(FragmentActivity context) {
        sContext = context;
        GoogleLoginUtil.getInstance().initialize(context);
        FacebookLoginUtil.initialize(context);
    }

    public static void loginWithGoogle(Activity activity) {
        loginSelected = LoginMethod.Google;
        GoogleLoginUtil.getInstance().signIn(activity);
    }

    public static void loginWithFacebook(Activity activity, List<String> user_status) {
        loginSelected = LoginMethod.Facebook;
        FacebookLoginUtil.getInstance().logIn(activity, user_status);
    }

    public static void logoutWithGoogle() {
        GoogleLoginUtil.getInstance().signOut();
        SharedData.clearSharedPreference(sContext);
    }

    public static void logoutWithFacebook() {
        FacebookLoginUtil.getInstance().logout();
    }

    public static boolean isLoginedWithGoogle() {
        return GoogleLoginUtil.getInstance().isSignedIn(sContext);
    }

    public static boolean isLoginedWithFacebook() {
        return FacebookLoginUtil.getInstance().isLogined();
    }

    public static void setGoogleLoginResultCallback(GoogleLoginInResultCallback callback) {
        GoogleLoginUtil.getInstance().setLoginResultCallback(callback);
    }

    public static void setFacebookLoginResultCallback(FacebookCallback<LoginResult> callback) {
        FacebookLoginUtil.getInstance().setLoginResultCallback(callback);
    }

    public static void setGoogleLogoutResultCallback(ResultCallback<Status> logoutResultCallback) {
        GoogleLoginUtil.getInstance().setLogoutResultCallback(logoutResultCallback);
    }

    public static void setFacebookLogoutResultCallback(FacebookLoginUtil.Callback callback) {
        FacebookLoginUtil.getInstance().setLogoutResultCallback(callback);
    }

    public static void onResume() {
        FacebookLoginUtil.getInstance().onResume();
    }
    public static void onPause() {
        FacebookLoginUtil.getInstance().onPause();
    }
    public static void onDestroy() {
        FacebookLoginUtil.getInstance().onDestroy();
    }
}
