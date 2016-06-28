package com.devsh.androidlogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.devsh.androidlogin.library.FacebookLoginUtil;
import com.devsh.androidlogin.library.callback.GoogleLoginInResultCallback;
import com.devsh.androidlogin.library.AndroidLogin;
import com.devsh.androidlogin.library.data.SharedData;
import com.devsh.androidlogin.library.server.ServerLogin;
import com.devsh.androidlogin.library.server.ServerLoginResultCallback;
import com.facebook.AccessToken;
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

    private String TAG = "AndroidLogin";
    private String GOOGLE_SERVER_CLIENT_ID = "486150556496-2h0adv5kgeesn7s6303ri6kbn6cncpu5.apps.googleusercontent.com";
    private String TWITTER_API_KEY = "stBDcBVHLNu1nD1f2FQvtfMkm";
    private String TWITTER_SECRET_KEY = "T6Lc9dwRoK6FedzI5NDtYSWQs3UvRIyLVXws8OShFeU7mL358O";

    private LoginButton btnFacebookSignIn;
    private Button btnGoogleSignin;
    private TwitterLoginButton btnTwitterLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ServerLogin.initialize(Common.API_BASE_URL);
        AndroidLogin.initialize(this, TWITTER_API_KEY, TWITTER_SECRET_KEY, GOOGLE_SERVER_CLIENT_ID); // twitter, google, facebook
        setContentView(R.layout.activity_main);

        if (AndroidLogin.isLogined()) {
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            AndroidLogin.loggined(this, intent);
        }

        btnTwitterLogin = (TwitterLoginButton) findViewById(R.id.btnTwitterLogin);
        AndroidLogin.setTwitterLoginResultCallback(btnTwitterLogin, new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {
                tryLogin();
                updateUI();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
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

        btnFacebookSignIn = (LoginButton) findViewById(R.id.btnFacebookSignIn);
        btnFacebookSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AndroidLogin.isLoginedWithFacebook()) {
                    AndroidLogin.logoutWithFacebook();
                } else {
                    AndroidLogin.loginWithFacebook(MainActivity.this, Arrays.asList("user_status"));
                }
            }
        });


        // Google Login
        AndroidLogin.setGoogleLoginResultCallback(new GoogleLoginInResultCallback() {
            @Override
            public void onSuccess(GoogleSignInResult result) {
                tryLogin();

                updateUI();
            }

            @Override
            public void onFail(GoogleSignInResult result) {
                Log.i(TAG, "onFail: " + result);

                updateUI();
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
        // 1. LoginResultCallback
        AndroidLogin.setFacebookLoginResultCallback(new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "onSuccess: " + loginResult);
                tryLogin();
                updateUI();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: ");

                updateUI();
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "onError: " + error);

            }
        });


        AndroidLogin.setFacebookLogoutResultCallback(new FacebookLoginUtil.Callback() {
            @Override
            public void onCallback(AccessToken currentToken) {
                updateUI();
            }
        });

        // 2. Access Token method
//        FacebookLoginUtil.getInstance().setLoginCallbackByAccessToken(new FacebookLoginUtil.Callback() {
//            @Override
//            public void onCallback(AccessToken currentToken) {
//                Log.i(TAG, "onCallback: loginCallback");
//            }
//        });
//
//        FacebookLoginUtil.getInstance().setLogoutCallbackByAccessToken(new FacebookLoginUtil.Callback() {
//            @Override
//            public void onCallback(AccessToken currentToken) {
//                Log.i(TAG, "onCallback: " + currentToken);
//                updateUI();
//            }
//        });
//
//        FacebookLoginUtil.getInstance().setUpdateTokenCallbackByAccessToken(new FacebookLoginUtil.Callback() {
//            @Override
//            public void onCallback(AccessToken currentToken) {
//                Log.i(TAG, "onCallback: updateTokenCallback");
//            }
//        });

        updateUI();
    }

    public void tryLogin() {
        Log.i(TAG, "id:" + SharedData.getAccountId(getApplicationContext()));
        Log.i(TAG, "userName:" + SharedData.getAccountUserName(getApplicationContext()));
        Log.i(TAG, "userEmail:" + SharedData.getAccountUserEmail(getApplicationContext()));
        Log.i(TAG, "userPhoto:" + SharedData.getAccountUserPhoto(getApplicationContext()));
        Log.i(TAG, "token:" + SharedData.getAccountIdToken(getApplicationContext()));

        if (SharedData.getServerToken(getApplicationContext()) == null) {
            // Try Login
            ServerLogin.login(getApplicationContext(), new ServerLoginResultCallback() {
                @Override
                public void onSuccess(String apiToken) {
                    SharedData.putServerToken(getApplicationContext(), apiToken);
                    Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFail(String error) {
                    Log.i(TAG, "onFail : " + error);

                }
            });
        } else {
            // already token is there.
            Log.i(TAG, "continues login");

        }
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
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        AndroidLogin.onActivityResult(requestCode, resultCode, data);
    }
}
