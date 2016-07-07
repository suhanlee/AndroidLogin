package com.devsh.androidlogin;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.devsh.androidlogin.library.FacebookLoginUtil;
import com.devsh.androidlogin.library.callback.GoogleLoginInResultCallback;
import com.devsh.androidlogin.library.AndroidLogin;
import com.devsh.androidlogin.library.data.SharedData;
import com.devsh.androidlogin.server.ServerLogin;
import com.devsh.androidlogin.server.ServerLoginResultCallback;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private String TAG = "AndroidLogin";

    private Button btnFacebookSignIn;
    private Button btnGoogleSignin;
    private TwitterLoginButton btnTwitterLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.devsh.androidlogin",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        ServerLogin.initialize(Common.API_BASE_URL);
        AndroidLogin.initialize(this,
                getString(R.string.twitter_api_key),
                getString(R.string.twitter_secret_key),
                getString(R.string.google_web_client_id)); // twitter, google, facebook

        setContentView(R.layout.activity_main);

        if (AndroidLogin.isLogined()) {
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            AndroidLogin.loggined(this, intent);
            finish();
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

        btnFacebookSignIn = (Button) findViewById(R.id.btnFacebookSignIn);
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

//        // 2. Access Token method
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
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            // already token is there.
            Log.i(TAG, "continues login");
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
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
