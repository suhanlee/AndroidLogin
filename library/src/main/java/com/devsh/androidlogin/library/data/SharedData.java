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

package com.devsh.androidlogin.library.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

public class SharedData {

    public final static String PROVIDER_GOOGLE = "google_oauth2";
    public final static String PROVIDER_TWITTER = "twitter";
    public final static String PROVIDER_FACEBOOK = "facebook";

    private static final String ACCOUNT_PROVIDER_KEY = "PROVIDER_KEY";

    // Account Information
    private static String ACCOUNT_ID_TOKEN_KEY = "ACCOUNT_ID_TOKEN_KEY";
    private static String ACCOUNT_ID_KEY = "ACCOUNT_ID_KEY";
    private static String ACCOUNT_USER_NAME_KEY = "ACCOUNT_USER_NAME_KEY";
    private static String ACCOUNT_USER_EMAIL_KEY = "ACCOUNT_USER_EMAIL_KEY";
    private static String ACCOUNT_USER_PHOTO_KEY = "ACCOUNT_USER_PHOTO_KEY";
    private static String SERVER_TOKEN = "SERVER_TOKEN";
    private static String PUSH_REGISTRATION_TOKEN ="PUSH_REGISTRATION_TOKEN_KEY";

    private static String ACCOUNT_LOGGED_IN_KEY = "ACCOUNT_IS_LOGGED_IN_KEY";

    public static void putPushRegistrationToken(Context context, String registrationToken) {
        putSharedPreferenceString(context, PUSH_REGISTRATION_TOKEN, registrationToken);
    }

    public static String getPushRegistrationToken(Context context) {
        return getSharedPreferenceString(context, PUSH_REGISTRATION_TOKEN);
    }

    public static void putServerToken(Context context, String serverToken) {
        putSharedPreferenceString(context, SERVER_TOKEN, serverToken);
    }

    public static String getServerToken(Context context) {
        return getSharedPreferenceString(context, SERVER_TOKEN);
    }

    public static void putAccountUserEmail(Context context, String userEmail) {
        putSharedPreferenceString(context, ACCOUNT_USER_EMAIL_KEY, userEmail);
    }

    public static String getAccountUserEmail(Context context) {
        return getSharedPreferenceString(context, ACCOUNT_USER_EMAIL_KEY);
    }

    public static void putAccountUserPhoto(Context context, String userPhoto) {
        putSharedPreferenceString(context, ACCOUNT_USER_PHOTO_KEY, userPhoto);
    }

    public static String getAccountUserPhoto(Context context) {
        return getSharedPreferenceString(context, ACCOUNT_USER_PHOTO_KEY);
    }

    public static void putAccountUserName(Context context, String userName) {
        putSharedPreferenceString(context, ACCOUNT_USER_NAME_KEY, userName);
    }

    public static String getAccountUserName(Context context) {
        return getSharedPreferenceString(context, ACCOUNT_USER_NAME_KEY);
    }

    public static void putAccountId(Context context, String id) {
        putSharedPreferenceString(context, ACCOUNT_ID_KEY, id);
    }

    public static void putAccountIdToken(Context context, String idToken) {
        putSharedPreferenceString(context, ACCOUNT_ID_TOKEN_KEY, idToken);
    }

    public static String getAccountId(Context context) {
        return getSharedPreferenceString(context, ACCOUNT_ID_KEY);
    }

    public static String getAccountIdToken(Context context) {
        return getSharedPreferenceString(context, ACCOUNT_ID_TOKEN_KEY);
    }

    private static String getSharedPreferenceString(Context context, String key) {
        return getSharedPreferenceString(context, key, null);
    }

    private static String getSharedPreferenceString(Context context, String key, String defaultReturnValue) {
        try{
            SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return mSharedPrefs.getString(key, defaultReturnValue);
        }catch(NullPointerException e){
            e.printStackTrace();
            return defaultReturnValue;
        }
    }

    public static void putSharedPreferenceString(Context context, String key, String value){
        putSharedPreference(context, key, value);
    }

    private static Boolean getSharedPreferenceBoolean(Context context, String key) {
        return getSharedPreferenceBoolean(context, key, false);
    }

    private static Boolean getSharedPreferenceBoolean(Context context, String key, Boolean defaultReturnValue) {
        try{
            SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return mSharedPrefs.getBoolean(key, defaultReturnValue);
        }catch(NullPointerException e){
            e.printStackTrace();
            return defaultReturnValue;
        }
    }

    public static void putSharedPreferenceBoolean(Context context, String key, Boolean value){
        putSharedPreference(context, key, value);
    }

    private static void putSharedPreference(Context context, String key, Object value) {
        if (context == null) {
            return;
        }
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPrefs.edit();

        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            throw new IllegalArgumentException("Not supported data type");
        }
        editor.commit();
    }

    public static void clearSharedPreference(Context context) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        String registration_token = getPushRegistrationToken(context);
        editor.clear();
        editor.commit();

        if (registration_token != null) {
            putPushRegistrationToken(context, registration_token);
        }
    }

    public static String getAccountProvider(Context context) {
        return getSharedPreferenceString(context, ACCOUNT_PROVIDER_KEY);
    }

    public static void putAccountProvider(Context context, String provider) {
        putSharedPreferenceString(context, ACCOUNT_PROVIDER_KEY, provider);
    }

    public static boolean isLoggedIn(Context context) {
        return getSharedPreferenceBoolean(context, ACCOUNT_LOGGED_IN_KEY);
    }

    public static void setLoggedIn(Context context, boolean isLoggedIn) {
        putSharedPreferenceBoolean(context, ACCOUNT_LOGGED_IN_KEY, isLoggedIn);
    }
}
