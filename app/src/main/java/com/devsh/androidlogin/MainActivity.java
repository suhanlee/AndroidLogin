package com.devsh.androidlogin;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.devsh.androidlogin.common.Common;
import com.devsh.androidlogin.event.PushRegisterCompletedEvent;
import com.devsh.androidlogin.gcm.RegistrationIntentService;
import com.devsh.androidlogin.library.FacebookLoginUtil;
import com.devsh.androidlogin.library.callback.GoogleLoginInResultCallback;
import com.devsh.androidlogin.library.AndroidLogin;
import com.devsh.androidlogin.library.data.SharedData;
import com.devsh.androidlogin.login.ServerLoginServiceController;
import com.devsh.androidlogin.login.ServerLoginResultCallback;
import com.devsh.androidlogin.library.NetworkUtil;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private String TAG = "AndroidLogin";

    private Button btnFacebookSignIn;
    private Button btnGoogleSignin;
    private TwitterLoginButton btnTwitterLogin;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean isLoginReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intialize();
        setContentView(R.layout.activity_main);

        if (AndroidLogin.isLocalLogined()) {
            tryLogin();
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
                if (AndroidLogin.isLoginedWithGoogle()) {
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

        // Facebook Logout
        AndroidLogin.setFacebookLogoutResultCallback(new FacebookLoginUtil.Callback() {
            @Override
            public void onCallback(AccessToken currentToken) {
                updateUI();
            }
        });

        updateUI();
    }

    private void intialize() {
        isLoginReady = false;
        EventBus.getDefault().register(this);
        setUpPush();

        ServerLoginServiceController.initialize(Common.API_BASE_URL);
        AndroidLogin.initialize(this,
                getString(R.string.twitter_api_key),
                getString(R.string.twitter_secret_key),
                getString(R.string.google_web_client_id)); // twitter, google, facebook
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PushRegisterCompletedEvent event) {
        Toast.makeText(getApplicationContext(), event.token, Toast.LENGTH_SHORT).show();
        String token = SharedData.getPushRegistrationToken(this);
        if (token != null) {
            isLoginReady = true;
        }
    }

    private void setUpPush() {
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    public void tryLogin() {
        if (!NetworkUtil.isOnline(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Network 연결 확인 요망", Toast.LENGTH_LONG).show();
            return;
        }
        Log.i(TAG, "id:" + SharedData.getAccountId(getApplicationContext()));
        Log.i(TAG, "userName:" + SharedData.getAccountUserName(getApplicationContext()));
        Log.i(TAG, "userEmail:" + SharedData.getAccountUserEmail(getApplicationContext()));
        Log.i(TAG, "userPhoto:" + SharedData.getAccountUserPhoto(getApplicationContext()));
        Log.i(TAG, "token:" + SharedData.getAccountIdToken(getApplicationContext()));

            // Try Login
            ServerLoginServiceController.login(getApplicationContext(), new ServerLoginResultCallback() {
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
                    Toast.makeText(getApplicationContext(), "Server Error" + error, Toast.LENGTH_SHORT).show();

                }
            });
    }

    public void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AndroidLogin.isLoginedWithGoogle()) {
                    btnGoogleSignin.setText("Google Logout");
                } else {
                    btnGoogleSignin.setText("Google Login");
                }

                if (AndroidLogin.isLoginedWithFacebook()) {
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

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not suppored");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
