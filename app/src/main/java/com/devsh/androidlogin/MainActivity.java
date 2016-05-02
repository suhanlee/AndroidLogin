package com.devsh.androidlogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.devsh.androidlogin.library.FacebookUtil;
import com.devsh.androidlogin.library.GoogleSignInUtil;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInUtil.getInstance().initialize(this);
        FacebookUtil.initialize(this);

        Button btnGoogleSignin = (Button) findViewById(R.id.btnGoogleSignIn);
        btnGoogleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInUtil.getInstance().signIn(MainActivity.this);
            }
        });

        Button btnFacebookSignIn = (Button) findViewById(R.id.btnFacebookSignIn);
        btnFacebookSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookUtil.getInstance().logIn(MainActivity.this, Arrays.asList("user_status"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        GoogleSignInUtil.getInstance().onActivityResult(requestCode, resultCode, data);
    }
}
