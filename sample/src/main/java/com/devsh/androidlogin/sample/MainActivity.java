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

package com.devsh.androidlogin.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.devsh.androidlogin.library.AndroidLogin;
import com.devsh.androidlogin.library.FacebookLoginUtil;
import com.devsh.androidlogin.library.callback.GoogleLoginInResultCallback;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LoginSample";
    private Button btnFacebookSignIn;
    private Button btnGoogleSignin;
    private TwitterLoginButton btnTwitterLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidLogin.initialize(this,
                getString(R.string.twitter_api_key),
                getString(R.string.twitter_secret_key),
                getString(R.string.google_web_client_id)); // twitter, google, facebook

        setContentView(R.layout.activity_main);

        if (AndroidLogin.isLogined()) {
//
        }

        btnTwitterLogin = (TwitterLoginButton) findViewById(R.id.btnTwitterLogin);
        AndroidLogin.setTwitterLoginResultCallback(btnTwitterLogin, new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {
                tryLogin();
                updateUI();
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
                Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        });

        btnGoogleSignin = (Button) findViewById(R.id.btnGoogleSignIn);
        btnGoogleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AndroidLogin.isLogined()) {
                    AndroidLogin.logoutWithGoogle();
                } else {
                    AndroidLogin.loginWithGoogle(MainActivity.this);
                }
            }
        });

        btnFacebookSignIn = (Button) findViewById(R.id.btnFacebookSignIn);
        btnFacebookSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AndroidLogin.isLoginedWithFacebook()) {
                    AndroidLogin.logoutWithFacebook();
                } else {
                    AndroidLogin.loginWithFacebook(MainActivity.this, Arrays.asList("public_profile"));
                }
            }
        });

        // Google Login
        AndroidLogin.setGoogleLoginResultCallback(new GoogleLoginInResultCallback() {
            @Override
            public void onSuccess(GoogleSignInResult result) {
                tryLogin();
                updateUI();
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(GoogleSignInResult result) {
                Log.i(TAG, "onFail: " + result.getStatus());
                updateUI();
                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG).show();
            }
        });

        // Google Logout
        AndroidLogin.setGoogleLogoutResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

                updateUI();
            }
        });

        // Facebook Login
        // LoginResultCallback
        AndroidLogin.setFacebookLoginResultCallback(new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "onSuccess: " + loginResult);
                tryLogin();
                updateUI();
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: ");

                updateUI();
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "onError: " + error);
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            }
        });

        AndroidLogin.setFacebookLogoutResultCallback(new FacebookLoginUtil.Callback() {
            @Override
            public void onCallback(AccessToken currentToken) {
                updateUI();
            }
        });


        updateUI();
    }

    private void tryLogin() {
        // trying... login

    }

    public void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AndroidLogin.isLogined()) {
                    btnGoogleSignin.setText("Google Logout");
                } else {
                    btnGoogleSignin.setText("Google Login");
                }

                if (AndroidLogin.isLogined()) {
                    btnFacebookSignIn.setText("Facebook Logout");
                } else {
                    btnFacebookSignIn.setText("Facebook Login");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        AndroidLogin.onActivityResult(requestCode, resultCode, data);
    }

}
