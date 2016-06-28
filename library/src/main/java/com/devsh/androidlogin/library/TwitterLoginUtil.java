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

import android.content.Context;
import android.content.Intent;

import com.devsh.androidlogin.library.data.SharedData;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class TwitterLoginUtil {
    private static TwitterLoginUtil sInstance;
    private static TwitterLoginButton sLoginButton;
    private TwitterLoginButton loginButton;
    private Callback<TwitterSession> callback;
    private Context context;

    public static TwitterLoginUtil getInstance() {
        if (sInstance == null) {
            sInstance = new TwitterLoginUtil();
        }
        return sInstance;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.loginButton.onActivityResult(requestCode, resultCode, data);
    }

    public void initialize(Context context) {
        this.context = context;
    }

    public void setLoginButton(TwitterLoginButton loginButton) {
        this.loginButton = loginButton;
        this.loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;

                SharedData.putAccountProvider(context, SharedData.PROVIDER_TWITTER);
                SharedData.putAccountIdToken(context, session.getAuthToken().token);
                SharedData.putAccountId(context, session.getUserId() +"");
                SharedData.putAccountUserName(context, session.getUserName());

                tryGetEmailAddress(session, result);
            }
            @Override
            public void failure(TwitterException exception) {
                if (callback != null) {
                    callback.failure(exception);
                }
            }
        });

    }

    private void tryGetEmailAddress(TwitterSession session, final Result<TwitterSession> twitterSessionResult) {
        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                // Do something with the result, which provides the email address
                SharedData.putAccountUserEmail(context, result.data);

                if (callback != null) {
                    callback.success(twitterSessionResult);
                }
            }

            @Override
            public void failure(TwitterException exception) {
                if (callback != null) {
                    callback.success(twitterSessionResult);
                }
            }
        });
    }

    public void setCallback(Callback<TwitterSession> callback) {
        this.callback = callback;
    }
}
